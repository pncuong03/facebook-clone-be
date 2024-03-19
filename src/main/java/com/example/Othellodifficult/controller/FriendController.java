package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.friends.FriendRequestOutput;
import com.example.Othellodifficult.dto.user.UserOutput;
import com.example.Othellodifficult.service.FriendsService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Danh sách bạn bè")
    @GetMapping("/list")
    public Page<UserOutput> getFriends(@RequestHeader("Authorization") String accessToken,
                                       @ParameterObject Pageable pageable){
        return friendsService.getFriends(accessToken, pageable);
    }

    @Operation(summary = "Gửi yêu cầu kết bạn")
    @PostMapping("/add")
    public void sendRequestAddFriends(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {
        friendsService.sendRequestAddFriend(id, accessToken);
    }

    @Operation(summary = "Đồng ý lời mời kết bạn")
    @PostMapping("/accept")
    public void acceptAddFriendRequest(@RequestParam Long id, @RequestHeader("Authorization") String accessToken) {
        friendsService.acceptAddFriendRequest(id, accessToken);
    }

    @Operation(summary = "Lấy danh sách lời mời kết bạn")
    @GetMapping("/request/list")
    public Page<FriendRequestOutput> getFriendRequests(@RequestHeader("Authorization") String accessToken,
                                                       @ParameterObject Pageable pageable){
        return friendsService.getFriendRequests(accessToken, pageable);
    }

    @Operation(summary = "Xóa bạn")
    @DeleteMapping("/delete")
    void deleteFriends(@RequestParam Long friendId, @RequestHeader("Authorization") String accessToken){
        friendsService.deleteFriend(friendId, accessToken);
    }

    @Operation(summary = "Từ chối lời mời kết bạn")
    @DeleteMapping("/reject")
    void rejectAddFriendRequest(@RequestParam Long senderId, @RequestHeader("Authorization") String accessToken){
        friendsService.rejectAddFriendRequest(senderId, accessToken);
    }
}
