package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.friends.FriendRequestOutput;
import com.example.Othellodifficult.entity.*;
import com.example.Othellodifficult.entity.friend.FriendMapEntity;
import com.example.Othellodifficult.entity.friend.FriendRequestEntity;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendsService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final FriendMapRepository friendMapRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

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

//        ChatEntity chatEntity = ChatEntity.builder()
//                .chatType(Common.USER)
//                .build();
//        chatRepository.save(chatEntity);
//
//        userChatRepository.save(UserChatMapEntity.builder()
//                .chatId(chatEntity.getId())
//                .userId(receiverId)
//                .build()
//        );
//        userChatRepository.save(UserChatMapEntity.builder()
//                .chatId(chatEntity.getId())
//                .userId(senderId)
//                .build()
//        );
    }

    @Transactional
    public void rejectAddFriendRequest(Long sendId, String token) {
        Long receiveId = TokenHelper.getUserIdFromToken(token);
        friendRequestRepository.deleteByReceiverIdAndSenderId(receiveId, sendId);
    }

    @Transactional(readOnly = true)
    public Page<FriendRequestOutput> getFriendRequests(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<FriendRequestEntity> friendRequestEntityPage = friendRequestRepository.findAllByReceiverId(userId, pageable);
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

//    public Page<FriendPerPageOutput> getFriendPerPage(String token, int pageNum) {
//        Pageable pageable = PageRequest.of(pageNum - 1, 3);
//        Long senderId = TokenHelper.getUserIdFromToken(token);
//        Page<FriendMapEntity> totalFriendMapEntity = friendMapRepository.findAllByUserId(senderId, pageable);
//        List<FriendPerPageOutput> friendPerPageOutputs = new ArrayList<>();
//        for (FriendMapEntity friendMapEntity : totalFriendMapEntity) {
//            Long friendId = null;
//            if (!friendMapEntity.getUserId1().equals(senderId)) {
//                friendId = friendMapEntity.getUserId1();
//            } else {
//                friendId = friendMapEntity.getUserId2();
//            }
//            UserEntity userEntity = userRepository.findById(friendId).get();
//            friendPerPageOutputs.add(FriendPerPageOutput.builder()
//                    .id(friendMapEntity.getId())
//                    .name(userEntity.getUsername())
//                    .userId(friendId)
//                    .build()
//            );
//        }
//        return new PageImpl<>(friendPerPageOutputs, pageable, totalFriendMapEntity.getTotalElements());
//    }

    @Transactional
    public void deleteFriend(Long friendId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        friendMapRepository.deleteAllByUserId1AndUserId2(userId, friendId);
        friendMapRepository.deleteAllByUserId1AndUserId2(friendId, userId);
    }
}
