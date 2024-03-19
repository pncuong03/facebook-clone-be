package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.message.MessageInput;
import com.example.Othellodifficult.dto.message.MessageOutput;
import com.example.Othellodifficult.entity.message.MessageEntity;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {
    MessageEntity getEntityFromInput(MessageInput messageInput);
    MessageOutput getOutputFromEntity(MessageEntity messageEntity);
}
