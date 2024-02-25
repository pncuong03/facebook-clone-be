package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.chat.ChatInput;
import com.example.Othellodifficult.entity.ChatEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatEntity getEntityFromInput(ChatInput chatInput);
}
