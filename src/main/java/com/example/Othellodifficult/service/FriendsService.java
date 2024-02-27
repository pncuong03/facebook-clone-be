package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.friends.FriendPerPageOutput;
import com.example.Othellodifficult.entity.*;
import com.example.Othellodifficult.entity.friend.FriendMapEntity;
import com.example.Othellodifficult.entity.friend.FriendRequestEntity;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendsService {
    private final FriendRequestReposiroty friendRequestReposiroty;
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
        friendRequestReposiroty.save(friendRequestEntity);
    }

    @Transactional
    public void acceptAddFriendRequest(Long senderId, String token) {
        Long receiverId = TokenHelper.getUserIdFromToken(token);
        if (Boolean.FALSE.equals(friendRequestReposiroty.existsBySenderIdAndReceiverId(senderId, receiverId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        friendMapRepository.save(FriendMapEntity.builder()
                .userId1(receiverId)
                .userId2(senderId)
                .build()
        );

        friendRequestReposiroty.deleteByReceiverIdAndSenderId(receiverId, senderId);

        ChatEntity chatEntity = ChatEntity.builder()
                .chatType(Common.USER)
                .build();
        chatRepository.save(chatEntity);

        userChatRepository.save(UserChatMapEntity.builder()
                .chatId(chatEntity.getId())
                .userId(receiverId)
                .build()
        );
        userChatRepository.save(UserChatMapEntity.builder()
                .chatId(chatEntity.getId())
                .userId(senderId)
                .build()
        );
    }

    @Transactional
    public void deleteAddFriendRequest(Long sendId, String token) {
        Long receiveId = TokenHelper.getUserIdFromToken(token);
        friendRequestReposiroty.deleteByReceiverIdAndSenderId(receiveId, sendId);
    }

    public Page<FriendPerPageOutput> getFriendPerPage(String token, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, 3);
        Long senderId = TokenHelper.getUserIdFromToken(token);
        Page<FriendMapEntity> totalFriendMapEntity = friendMapRepository.findAllByUserId(senderId, pageable);
        List<FriendPerPageOutput> friendPerPageOutputs = new ArrayList<>();
        for (FriendMapEntity friendMapEntity : totalFriendMapEntity) {
            Long friendId = null;
            if (!friendMapEntity.getUserId1().equals(senderId)) {
                friendId = friendMapEntity.getUserId1();
            } else {
                friendId = friendMapEntity.getUserId2();
            }
            UserEntity userEntity = userRepository.findById(friendId).get();
            friendPerPageOutputs.add(FriendPerPageOutput.builder()
                    .id(friendMapEntity.getId())
                    .name(userEntity.getUsername())
                    .userId(friendId)
                    .build()
            );
        }
        return new PageImpl<>(friendPerPageOutputs, pageable, totalFriendMapEntity.getTotalElements());
    }

    public void deleteFriend(Long chatMapId) {
        friendMapRepository.deleteById(chatMapId);
    }
}
