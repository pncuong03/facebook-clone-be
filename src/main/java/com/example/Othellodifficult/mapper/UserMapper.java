package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.user.UserRequest;
import com.example.Othellodifficult.entity.UserEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity getEntityFromRequest(UserRequest signUpRequest);
}
