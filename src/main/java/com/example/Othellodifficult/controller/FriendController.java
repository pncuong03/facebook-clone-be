package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.friends.ListFriendOutput;
import com.example.Othellodifficult.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/friend")
@AllArgsConstructor
public class FriendController {
    private final FriendsService friendsService;

    @PostMapping("/add-friend")
    public void sendRequestAddFriends(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {
        friendsService.sendRequestAddFriend(id, accessToken);
    }

    @PostMapping("/accept")
    public void acceptAddFriendRequest(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {
        friendsService.acceptAddFriendRequest(id, accessToken);
    }

    @Operation(summary = "Get all list friends")
    @GetMapping("/get-list")
    Page <ListFriendOutput> getListFriends(@RequestHeader("Authorization") String accessToken,
                                           @RequestParam int pageNumber) {
        return friendsService.getListFriends(accessToken,pageNumber);
    }

    @Operation(summary = "Delete friend ")
    @DeleteMapping
    void deleteFriends(@RequestParam Long chatMapId){
        friendsService.deleteFriend(chatMapId);
    }

    @Operation(summary = "delete add friend request")
    @DeleteMapping("/delete-request")
    void deleteAddFriendRequest(@RequestParam Long senderId, @RequestHeader String accessToken){
        friendsService.deleteAddFriendRequest(senderId,accessToken);
    }
}
