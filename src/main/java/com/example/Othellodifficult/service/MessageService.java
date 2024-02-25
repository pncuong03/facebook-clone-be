package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.message.MessageInput;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.GroupChatEntity;
import com.example.Othellodifficult.entity.UserGroupChatEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.entity.message.MessageEntity;
import com.example.Othellodifficult.mapper.MessageMapper;
import com.example.Othellodifficult.repository.ChatRepository;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.repository.MessageRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public String send(MessageInput messageInput, String token) {
        Long senderId = TokenHelper.getUserIdFromToken(token);
        MessageEntity messageEntity = messageMapper.getEntityFromInput(messageInput);
        messageEntity.setSenderId(senderId);
        messageEntity.setCreatedAt(LocalDateTime.now());

        messageRepository.save(messageEntity);
        // update chatEntity newestChatTime = LocalDateTime.now()
        CompletableFuture.runAsync(() -> {
            List<UserGroupChatEntity> userChatEntities =

            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .eventType(Common.MESSAGE)
//                                .userId(messageInput)
                            .build()
            );
        });
        return messageInput.getMessage();
    }


}
