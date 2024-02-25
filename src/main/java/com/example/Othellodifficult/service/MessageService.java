package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.message.MessageInput;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.UserChatEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.entity.message.MessageEntity;
import com.example.Othellodifficult.mapper.MessageMapper;
import com.example.Othellodifficult.repository.ChatRepository;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.repository.MessageRepository;
import com.example.Othellodifficult.repository.UserChatRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    @Transactional
    public String send(MessageInput messageInput, String token) {
        Long senderId = TokenHelper.getUserIdFromToken(token);
        MessageEntity messageEntity = messageMapper.getEntityFromInput(messageInput);
        messageEntity.setSenderId(senderId);
        messageEntity.setCreatedAt(LocalDateTime.now());

        messageRepository.save(messageEntity);
        // update chatEntity newestChatTime = LocalDateTime.now()
        CompletableFuture.runAsync(() -> {
            ChatEntity chatEntity = chatRepository.findById(messageInput.getChatId()).get();
            chatEntity.setNewestChatTime(LocalDateTime.now());
            chatRepository.save(chatEntity);

            List<UserChatEntity> userChatEntities = userChatRepository.findAllByGroupId(messageInput.getChatId()).stream()
                    .filter(userChatEntity -> !userChatEntity.getUserId().equals(senderId))
                    .collect(Collectors.toList());
            if (!userChatEntities.isEmpty()){
                for (UserChatEntity userChatEntity : userChatEntities){
                    eventNotificationRepository.save(
                            EventNotificationEntity.builder()
                                    .eventType(Common.MESSAGE)
                                    .userId(userChatEntity.getUserId())
                                    .build()
                    );
                }
            }
        });
        return messageInput.getMessage();
    }


}
