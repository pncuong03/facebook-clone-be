package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.user.*;
import com.example.Othellodifficult.service.FriendsService;
import com.example.Othellodifficult.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final FriendsService friendsService;

    @Operation(summary = "Tìm danh sách người dùng trên app")
    @GetMapping("/list")
    public Page<FriendSearchingOutput> findUsers(@RequestParam(required = false) String search,
                                                 @RequestHeader(value = Common.AUTHORIZATION) String accessToken,
                                                 @ParameterObject Pageable pageable){
        return friendsService.findUsers(search, accessToken, pageable);
    }

    @Operation(summary = "Lấy thông tin cá nhân")
    @GetMapping
    public UserOutputV2 getUserInformation(@RequestHeader(value = Common.AUTHORIZATION) String accessToken){
        return userService.getUserInformation(accessToken);
    }

    @Operation(summary = "Thay đổi thông tin cá nhân")
    @PostMapping("/change-user-information")
    public void changeUserInformation(@RequestBody @Valid ChangeInfoUserRequest changeInfoUserRequest,
                                      @RequestHeader(value = Common.AUTHORIZATION) String accessToken){
        userService.changeUserInformation(changeInfoUserRequest, accessToken);
    }

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("sign-up")
    public ResponseEntity signUp(@RequestBody UserRequest signUpRequest){
        return new ResponseEntity(new TokenResponse( userService.signUp(signUpRequest)), HttpStatus.OK);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("log-in")
    public ResponseEntity logIn(@RequestBody @Valid UserRequest logInRequest){
        return new ResponseEntity(new TokenResponse(userService.logIn(logInRequest)), HttpStatus.OK);
    }
}
