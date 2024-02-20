package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.user.UserRequest;
import com.example.Othellodifficult.entity.UserEntity;

public class UserMapper {
    public static UserEntity getEntityFromRequest(UserRequest signUpRequest){
        return UserEntity.builder()
                .username(signUpRequest.getUsername())
                .password(signUpRequest.getPassword())
                .build();
    }
}
