package com.example.Othellodifficult.service;

import com.example.Othellodifficult.base.filter.Filter;
import com.example.Othellodifficult.common.Common;
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

        Map<Long, Long> finalFriendMap = friendMap;
        return userEntities.map(
                userEntity -> {
                    FriendSearchingOutput friendSearching = userMapper.getFriendSearchingFrom(userEntity);
                    friendSearching.setIsFriend(finalFriendMap.containsKey(friendSearching.getId()));
                    return friendSearching;
                }
        );
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
                        .build()
        );


        chatRepository.save(
                ChatEntity.builder()
                        .name(sender.getFullName())
                        .imageUrl(sender.getImageUrl())
                        .chatType(Common.USER)
                        .userId2(receiverId)
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
    }
}
