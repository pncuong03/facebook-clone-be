package com.example.Othellodifficult.service;

import com.example.Othellodifficult.entity.FriendRequestEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.repository.FriendRequestReposiroty;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendsService {
    private final FriendRequestReposiroty friendRequestReposiroty;
    private final UserRepository userRepository;

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
                .createdAt(new Date())
                .build();
        friendRequestReposiroty.save(friendRequestEntity);
    }

    @Transactional
    public void acceptAddFriendRequest(String token, Long friendId) {
        Long sendId = TokenHelper.getUserIdFromToken(token);
        // accept then delte table friendRequest
    }
}
