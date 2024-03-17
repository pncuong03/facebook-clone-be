package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.user.ChangeInfoUserRequest;
import com.example.Othellodifficult.dto.user.FriendSearchingOutput;
import com.example.Othellodifficult.dto.user.UserOutputV2;
import com.example.Othellodifficult.dto.user.UserRequest;
import com.example.Othellodifficult.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper
public interface UserMapper {
    FriendSearchingOutput getFriendSearchingFrom(UserEntity userEntity);
    UserOutputV2 getOutputFromEntity(UserEntity userEntity);
    UserEntity getEntityFromRequest(UserRequest signUpRequest);
    void updateEntityFromInput(@MappingTarget UserEntity userEntity, ChangeInfoUserRequest changeInfoUserRequest);
}
