package com.example.Othellodifficult.service;

import com.example.Othellodifficult.base.filter.Filter;
import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.friends.FriendInforOutput;
import com.example.Othellodifficult.dto.friends.FriendPerPageOutput;
import com.example.Othellodifficult.dto.friends.FriendRequestOutput;
import com.example.Othellodifficult.dto.user.FriendSearchingOutput;
import com.example.Othellodifficult.dto.user.UserOutput;
import com.example.Othellodifficult.entity.*;
import com.example.Othellodifficult.entity.friend.FriendMapEntity;
import com.example.Othellodifficult.entity.friend.FriendRequestEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.mapper.UserMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.EventHelper;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendsService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendMapRepository friendMapRepository;
    private final EventNotificationRepository eventNotificationRepository;
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;
    private final UserMapper userMapper;
    private final NotificationRepository notificationRepository;
    private final CustomRepository customRepository;

    @Transactional(readOnly = true)
    public FriendInforOutput getFriendInformation(String accessToken, Long checkId){
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = userRepository.findById(checkId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
        FriendInforOutput friendInforOutput = userMapper.getFriendInforFromEntity(userEntity);
        if(Objects.nonNull(friendMapRepository.findByUserId1AndUserId2(userId,checkId))){
            ChatEntity chatEntity = chatRepository.findByUserId(userId,checkId);
            friendInforOutput.setState(Common.FRIEND);
            friendInforOutput.setChatId(chatEntity.getId());
        }else{
            if(Boolean.TRUE.equals(friendRequestRepository.existsBySenderIdAndReceiverId(userId,checkId))
            || Boolean.TRUE.equals(friendRequestRepository.existsBySenderIdAndReceiverId(checkId,userId)) ){
                friendInforOutput.setState(Common.REQUESTING);
                friendInforOutput.setChatId(null);
            }else{
                friendInforOutput.setState(Common.STRANGER);
                friendInforOutput.setChatId(null);
            }
        }
        return friendInforOutput;
    }

    @Transactional(readOnly = true)
    public Page<UserOutput> getFriendBySearch(String accessToken, String search, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<FriendMapEntity> friendMapEntities = friendMapRepository.findAllByUserId(userId, pageable);
        List<Long> friendIds = new ArrayList<>();
        if (Objects.isNull(friendMapEntities) || friendMapEntities.isEmpty()) {
            return Page.empty();
        }

        for (FriendMapEntity friendMapEntity : friendMapEntities) {
            friendIds.add(friendMapEntity.getUserId1());
            friendIds.add(friendMapEntity.getUserId2());
        }
        friendIds = friendIds.stream()
                .filter(friendId -> !friendId.equals(userId))
                .distinct()
                .collect(Collectors.toList());
        if(Objects.isNull(search)){
            return userRepository.findAllByIdIn(friendIds, pageable).map(
                    userEntity ->{
                        return UserOutput.builder()
                                .id(userEntity.getId())
                                .imageUrl(userEntity.getImageUrl())
                                .fullName(userEntity.getFullName())
                                .build();
                    });
        }
        Page<UserEntity> userEntities = Filter.builder(UserEntity.class, entityManager)
                .search()
                .isContain("fullName", search)
                .filter()
                .isIn("id", friendIds)
                .getPage(pageable);
        return userEntities.map(
                userEntity ->{
                    return UserOutput.builder()
                            .id(userEntity.getId())
                            .imageUrl(userEntity.getImageUrl())
                            .fullName(userEntity.getFullName())
                            .build();
                });
    }

    private Page<FriendSearchingOutput> setIsFriendOrHasRequestFriendForUsers(Long userId,
                                                                              Page<FriendSearchingOutput> users){
        if (Objects.isNull(users)){
            return Page.empty();
        }
        List<Long> userIds = users.stream().map(FriendSearchingOutput::getId).collect(Collectors.toList());
        // lấy map những thằng mình đã send friend request nhưng nó chưa đồng ý
        Map<Long, Long> userSendRequestMap = friendRequestRepository.findAllBySenderIdAndReceiverIdIn(userId, userIds).stream()
                .collect(Collectors.toMap(FriendRequestEntity::getReceiverId, FriendRequestEntity::getSenderId));
        // Lấy map những thằng đã send friend request nhưng mình chưa đồng ý
        Map<Long, Long> userReceiverRequestMap = friendRequestRepository.findAllBySenderIdInAndReceiverId(userIds, userId).stream()
                .collect(Collectors.toMap(FriendRequestEntity::getSenderId, FriendRequestEntity::getReceiverId));
        // Lấy những thằng đã là bạn bè mình rồi
        List<FriendMapEntity> friendMapEntities = friendMapRepository.findAllByUserId(userId);
        Map<Long, Long> friendMap = friendMapEntities.stream().map(
                friendMapEntity -> {
                    if (userId.equals(friendMapEntity.getUserId1())){
                        return friendMapEntity.getUserId2();
                    }
                    return friendMapEntity.getUserId1();
                }
        ).collect(Collectors.toMap(Function.identity(), Function.identity()));

        return users.map(friendSearchingOutput -> {
            if (userSendRequestMap.containsKey(friendSearchingOutput.getId())){
                friendSearchingOutput.setHadSendFriendRequest(true);
            }
            else {
                friendSearchingOutput.setHadSendFriendRequest(false);
            }

            if (userReceiverRequestMap.containsKey(friendSearchingOutput.getId())){
                friendSearchingOutput.setHadReceiverFriendRequest(true);
            }
            else {
                friendSearchingOutput.setHadReceiverFriendRequest(false);
            }

            if (friendMap.containsKey(friendSearchingOutput.getId())){
                friendSearchingOutput.setIsFriend(true);
            }
            else {
                friendSearchingOutput.setIsFriend(false);
            }
            return friendSearchingOutput;
        });
    }

    @Transactional(readOnly = true)
    public Page<FriendSearchingOutput> findUsers(String search, String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<UserEntity> userEntities = Filter.builder(UserEntity.class, entityManager)
                .search()
                .isContain("fullName", search)
                .filter()
                .isNotIn("id", Arrays.asList(userId))
                .getPage(pageable);

        if (userEntities.isEmpty()) {
            return Page.empty();
        }
        Map<Long, Long> friendMap = new HashMap<>();
        List<FriendMapEntity> friendMapEntities = friendMapRepository.findAllByUserId(userId);
        if (Objects.nonNull(friendMapEntities) && !friendMapEntities.isEmpty()) {
            friendMap = friendMapEntities.stream()
                    .distinct()
                    .collect(Collectors.toMap(FriendMapEntity::getId, FriendMapEntity::getId));
        }

        Map<Long, Long> friendRequestMap = new HashMap<>();
        List<FriendRequestEntity> friendRequestEntities = friendRequestRepository.findAllBySenderId(userId);
        if (Objects.nonNull(friendRequestEntities) && !friendMapEntities.isEmpty()) {
            friendRequestMap = friendRequestEntities.stream().collect(Collectors.toMap(FriendRequestEntity::getReceiverId, FriendRequestEntity::getSenderId));
        }

        Map<Long, Long> finalFriendMap = friendMap;
        Map<Long, Long> finalFriendRequestMap = friendRequestMap;
        Page<FriendSearchingOutput> friendSearchingOutputs = userEntities.map(
                userEntity -> {
                    FriendSearchingOutput friendSearching = userMapper.getFriendSearchingFrom(userEntity);
                    friendSearching.setIsFriend(finalFriendMap.containsKey(friendSearching.getId()));
                    friendSearching.setHadSendFriendRequest(finalFriendRequestMap.containsKey(friendSearching.getId()));
                    return friendSearching;
                }
        );

        return setIsFriendOrHasRequestFriendForUsers(userId, friendSearchingOutputs);
    }

    @Transactional(readOnly = true)
    public Page<UserOutput> getFriends(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<FriendMapEntity> friendMapEntities = friendMapRepository.findAllByUserId(userId, pageable);
        if (Objects.isNull(friendMapEntities) || friendMapEntities.isEmpty()) {
            return Page.empty();
        }

        List<Long> friendIds = new ArrayList<>();
        for (FriendMapEntity friendMapEntity : friendMapEntities) {
            friendIds.add(friendMapEntity.getUserId1());
            friendIds.add(friendMapEntity.getUserId2());
        }
        friendIds = friendIds.stream()
                .filter(friendId -> !friendId.equals(userId))
                .distinct()
                .collect(Collectors.toList());

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(friendIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return friendMapEntities.map(
                friendMapEntity -> {
                    UserEntity userEntity = null;
                    if (userEntityMap.containsKey(friendMapEntity.getUserId1())) {
                        userEntity = userEntityMap.get(friendMapEntity.getUserId1());
                    } else {
                        userEntity = userEntityMap.get(friendMapEntity.getUserId2());
                    }
                    return UserOutput.builder()
                            .id(userEntity.getId())
                            .imageUrl(userEntity.getImageUrl())
                            .fullName(userEntity.getFullName())
                            .build();
                }
        );
    }

    @Transactional
    public void sendRequestAddFriend(Long receiveId, String accessToken) {
        Long senderId = TokenHelper.getUserIdFromToken(accessToken);
        if (receiveId.equals(senderId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        // Neu da gui yeu cau roi thi k dc gui nua -- notDone
        FriendRequestEntity friendRequestEntity = FriendRequestEntity.builder()
                .senderId(senderId)
                .receiverId(receiveId)
                .createdAt(LocalDateTime.now())
                .build();
        friendRequestRepository.save(friendRequestEntity);
        CompletableFuture.runAsync(() -> {
            EventHelper.pushEventForUserByUserId(receiveId);
            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .userId(receiveId)
                            .eventType(Common.NOTIFICATION)
                            .state(Common.NEW_EVENT)
                            .build()
            );
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(receiveId)
                            .interactId(senderId)
                            .interactType(Common.FRIEND_REQUEST)
                            .hasSeen(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        });
    }
    @Transactional
    public void deleteSendFriendRequest(String accessToken, Long receiverId){
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if(Boolean.FALSE.equals(friendRequestRepository.existsBySenderIdAndReceiverId(userId,receiverId))){
            throw  new RuntimeException(Common.RECORD_NOT_FOUND);
        }
        friendRequestRepository.deleteByReceiverIdAndSenderId(receiverId, userId);
    }

    @Transactional
    public void acceptAddFriendRequest(Long senderId, String token) {
        Long receiverId = TokenHelper.getUserIdFromToken(token);
        if (Boolean.FALSE.equals(friendRequestRepository.existsBySenderIdAndReceiverId(senderId, receiverId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        friendMapRepository.save(FriendMapEntity.builder()
                .userId1(receiverId)
                .userId2(senderId)
                .build()
        );

        friendRequestRepository.deleteByReceiverIdAndSenderId(receiverId, senderId);

        CompletableFuture.runAsync(() -> {
            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .userId(senderId)
                            .eventType(Common.NOTIFICATION)
                            .state(Common.NEW_EVENT)
                            .build()
            );
            notificationRepository.save(
                    NotificationEntity.builder()
                            .userId(senderId)
                            .interactId(receiverId)
                            .interactType(Common.ACCEPT_FRIEND_REQUEST)
                            .hasSeen(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        });

        UserEntity receiver = customRepository.getUser(receiverId);
        UserEntity sender = customRepository.getUser(senderId);

        chatRepository.save(
                ChatEntity.builder()
                        .name(receiver.getFullName())
                        .imageUrl(receiver.getImageUrl())
                        .chatType(Common.USER)
                        .userId1(receiverId)
                        .userId2(senderId)
                        .newestUserId(1L)
                        .build()
        );


        chatRepository.save(
                ChatEntity.builder()
                        .name(sender.getFullName())
                        .imageUrl(sender.getImageUrl())
                        .chatType(Common.USER)
                        .userId2(receiverId)
                        .newestUserId(1L)
                        .userId1(senderId)
                        .build()
        );

        EventHelper.pushEventForUserByUserId(senderId);
    }

    @Transactional
    public void rejectAddFriendRequest(Long sendId, String token) {
        Long receiveId = TokenHelper.getUserIdFromToken(token);
        friendRequestRepository.deleteByReceiverIdAndSenderId(receiveId, sendId);
    }

    @Transactional(readOnly = true)
    public Page<FriendRequestOutput> getFriendRequests(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<FriendRequestEntity> friendRequestEntityPage = Filter.builder(FriendRequestEntity.class, entityManager)
                .filter()
                .isEqual("receiverId", userId)
                .orderBy("createdAt", Common.DESC)
                .getPage(pageable);
        if (Objects.isNull(friendRequestEntityPage) || friendRequestEntityPage.isEmpty()) {
            return Page.empty();
        }

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                friendRequestEntityPage.stream().map(FriendRequestEntity::getSenderId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return friendRequestEntityPage.map(
                friendRequestEntity -> {
                    FriendRequestOutput friendRequestOutput = new FriendRequestOutput();
                    if (userEntityMap.containsKey(friendRequestEntity.getSenderId())) {
                        UserEntity userEntity = userEntityMap.get(friendRequestEntity.getSenderId());
                        friendRequestOutput = FriendRequestOutput.builder()
                                .id(userEntity.getId())
                                .fullName(userEntity.getFullName())
                                .imageUrl(userEntity.getImageUrl())
                                .createdAt(friendRequestEntity.getCreatedAt())
                                .build();
                    }
                    return friendRequestOutput;
                }
        );
    }

    @Transactional
    public void deleteFriend(Long friendId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        friendMapRepository.deleteAllByUserId1AndUserId2(userId, friendId);
        friendMapRepository.deleteAllByUserId1AndUserId2(friendId, userId);
//        chatRepository.deleteAllByUserId1AndUserId2(userId, friendId);
//        chatRepository.deleteAllByUserId1AndUserId2(friendId, userId);
    }
}
