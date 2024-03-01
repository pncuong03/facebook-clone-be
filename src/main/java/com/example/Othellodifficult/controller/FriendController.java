package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.friends.FriendPerPageOutput;
import com.example.Othellodifficult.dto.friends.FriendRequestOutput;
import com.example.Othellodifficult.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/friend")
@AllArgsConstructor
public class FriendController {
    private final FriendsService friendsService;

    @PostMapping("/add")
    public void sendRequestAddFriends(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {
        friendsService.sendRequestAddFriend(id, accessToken);
    }

    @PostMapping("/accept")
    public void acceptAddFriendRequest(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {

        friendsService.acceptAddFriendRequest(id, accessToken);
    }

    @Operation(summary = "Get all list friends")
    @GetMapping("/list")
    public Page<FriendRequestOutput> getFriendRequests(@RequestHeader("Authorization") String accessToken,
                                                       @ParameterObject Pageable pageable){
        return friendsService.getFriendRequests(accessToken, pageable);
    }

    @Operation(summary = "Delete friend ")
    @DeleteMapping("/delete")
    void deleteFriends(@RequestParam Long friendId, @RequestHeader("Authorization") String accessToken){
        friendsService.deleteFriend(friendId, accessToken);
    }

    @Operation(summary = "delete add friend request")
    @DeleteMapping("/reject")
    void rejectAddFriendRequest(@RequestParam Long senderId, @RequestHeader("Authorization") String accessToken){
        friendsService.rejectAddFriendRequest(senderId, accessToken);
    }
}
