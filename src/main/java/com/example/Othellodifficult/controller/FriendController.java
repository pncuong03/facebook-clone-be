package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.service.FriendsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/friend")
@AllArgsConstructor
public class FriendController {
    private final FriendsService friendsService;
    @PostMapping("/add-friend")
    public void sendRequestAddFriends(@RequestParam Long id, @RequestHeader("Authorization") String accessToken){
        friendsService.sendRequestAddFriend(id, accessToken);
    }
    @PostMapping("/accept")
    public void acceptAddFriendRequest(@RequestParam Long id, @RequestHeader("Authorization") String accessToken){
        friendsService.acceptAddFriendRequest(id, accessToken);
    }
}
