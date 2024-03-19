package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.chat.ChatOutput;
import com.example.Othellodifficult.dto.chat.CreateGroupChatInput;
import com.example.Othellodifficult.entity.ChatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ChatMapper {
    ChatOutput getOutputFromEntity(ChatEntity chatEntity);
    ChatEntity getEntityFromInput(CreateGroupChatInput chatInput);
    void updateEntityFromInput(@MappingTarget ChatEntity chatEntity, CreateGroupChatInput chatInput);
}
