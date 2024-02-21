package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.groupchat.GroupChatInput;
import com.example.Othellodifficult.entity.GroupChatEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupChatMapper {
    GroupChatEntity getEntityFromInput(GroupChatInput groupChatInput);
}
