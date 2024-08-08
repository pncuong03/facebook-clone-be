package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.base.filter.Filter;
import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.user.*;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.service.FriendsService;
import com.example.Othellodifficult.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.List;

// https://blogs.perficient.com/2020/07/27/requestbody-and-multipart-on-spring-boot/
@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;
    private final FriendsService friendsService;
    private final EntityManager entityManager;

    @GetMapping("/test")
    public Page<UserEntity> getUsers(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String fullName,
                                     @RequestParam(required = false) List<Long> ids,
                                     @RequestParam(required = false) String gender,
                                     @ParameterObject Pageable pageable){
        return Filter.builder(UserEntity.class, entityManager)
                .search()
                .isContain("username", username)
                .isContain("fullName", fullName)

                .filter()
                .isIn("id", ids)
                .isEqual("gender", gender)
                .getPage(pageable);
    }

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

    // 2024-03-20T17:04:52.755Z
    @Operation(summary = "Thay đổi thông tin cá nhân")
    @PostMapping(value = "/change-user-information")
    public void changeUserInformation(@RequestPart("new_user_info") @Valid String changeInfoUserRequestString,
                                      @RequestHeader(value = Common.AUTHORIZATION) String accessToken) throws JsonProcessingException {
        ChangeInfoUserRequest changeInfoUserRequest;
        ObjectMapper objectMapper = new ObjectMapper();
        changeInfoUserRequest = objectMapper.readValue(changeInfoUserRequestString, ChangeInfoUserRequest.class);
        userService.changeUserInformation(changeInfoUserRequest, accessToken);
    }

    @Operation(summary = "Thay đổi ảnh bìa")
    @PostMapping(value = "/change-user-background", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public void changeUserBackground(
                                      @RequestHeader(value = Common.AUTHORIZATION) String accessToken,
                                      @RequestPart(value = "image_background", required = false) MultipartFile background)  {
        userService.changeUserBackground( accessToken, background);
    }

    @Operation(summary = "Thay đổi ảnh đại diện")
    @PostMapping(value = "/change-user-avatar", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public void changeUserAvatar(
            @RequestHeader(value = Common.AUTHORIZATION) String accessToken,
            @RequestPart(value = "image", required = false) MultipartFile avatar)  {
        userService.changeUserAvatar( accessToken, avatar);
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
