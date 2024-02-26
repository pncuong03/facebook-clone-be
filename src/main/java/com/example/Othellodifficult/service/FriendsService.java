package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.friends.ListFriendOutput;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendsService {
    private final FriendRequestReposiroty friendRequestReposiroty;
    private final UserRepository userRepository;
    private final FriendMapRepository friendMapRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    @Transactional
    public void sendRequestAddFriend(Long receiveId, String token) {
        // If the user is already the friends, then can't send the request-- not done
        Long senderId = TokenHelper.getUserIdFromToken(token);
        List<Long> listUserId = userRepository.findAll().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        if (!listUserId.contains(receiveId)) throw new RuntimeException("User doesn't exit!");
        if (receiveId.equals(senderId)) throw new RuntimeException("You can't add yourself");
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
        // accept then delete table friendRequests
        friendMapRepository.save(FriendMapEntity.builder()
                .userId_1(receiverId)
                .userId_2(senderId)
                .build()
        );
        /* When Accept the request, tokenId is a receiverId when send add friend request,
        so need to reverse */
        friendRequestReposiroty.deleteByReceiverIdAndSenderId(receiverId, senderId);
        ChatEntity chatEntity = ChatEntity.builder()
                .chatType(Common.USER)
                .build();
        chatRepository.save(chatEntity);

        userChatRepository.save(UserChatEntity.builder()
                .groupId(chatEntity.getId())
                .userId(receiverId)
                .build()
        );
        userChatRepository.save(UserChatEntity.builder()
                .groupId(chatEntity.getId())
                .userId(senderId)
                .build()
        );
    }

    public void deleteAddFriendRequest(Long sendId, String token ){
        Long receiveId = TokenHelper.getUserIdFromToken(token);
        friendRequestReposiroty.deleteByReceiverIdAndSenderId(receiveId,sendId);
    }
    public Page<ListFriendOutput> getListFriends(String token, int pageNum){
        Pageable pageable = PageRequest.of(pageNum -1, 3);
        Long senderId = TokenHelper.getUserIdFromToken(token);
        Page<FriendMapEntity> listFriendMapEntity = friendMapRepository.findAllByUserId(senderId,pageable);
        List<ListFriendOutput> listFriendOutputs = new ArrayList<>();
        for(FriendMapEntity friendMapEntity: listFriendMapEntity){
            Long friendId = null;
            if(!friendMapEntity.getUserId_1().equals(senderId)){
                friendId = friendMapEntity.getUserId_1();
            }else{
                friendId = friendMapEntity.getUserId_2();
            }
            UserEntity userEntity = userRepository.findById(friendId).get();
            listFriendOutputs.add(ListFriendOutput.builder()
                    .id(friendMapEntity.getId())
                            .name(userEntity.getUsername())
                    .userId(friendId)
                    .build()
            );
        }
        return new PageImpl<>(listFriendOutputs,pageable,listFriendMapEntity.getTotalElements());
    }
    public void deleteFriend(Long chatMapId){
            friendMapRepository.deleteById(chatMapId);
    }
}
