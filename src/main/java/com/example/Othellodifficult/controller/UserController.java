package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.user.ChangeInfoUserRequest;
import com.example.Othellodifficult.dto.user.TokenResponse;
import com.example.Othellodifficult.dto.user.UserRequest;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.service.UserService;
import com.example.Othellodifficult.token.TokenHelper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
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

    @PostMapping("/change-user-information")
    public void changeUserInformation(@RequestBody @Valid ChangeInfoUserRequest changeInfoUserRequest,
                                      @RequestHeader(value = "Authorization") String accessToken){
        userService.changeUserInformation(changeInfoUserRequest, accessToken);
    }

    @Operation(summary = "Đăng ký tài khoản")
    @PostMapping("sign-up")
    public String signUp(@RequestBody UserRequest signUpRequest){
        return userService.signUp(signUpRequest);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("log-in")
    public ResponseEntity logIn(@RequestBody @Valid UserRequest logInRequest){
        return new ResponseEntity(new TokenResponse(userService.logIn(logInRequest)), HttpStatus.OK);
    }
}
