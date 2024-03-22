package com.example.Othellodifficult.service;

import com.example.Othellodifficult.base.filter.Filter;
import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.chat.ChatOutput;
import com.example.Othellodifficult.dto.message.MessageOutput;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.PostEntity;
import com.example.Othellodifficult.entity.UserChatMapEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.entity.message.MessageEntity;
import com.example.Othellodifficult.mapper.ChatMapper;
import com.example.Othellodifficult.mapper.MessageMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.EventHelper;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChatService {
    private final EntityManager entityManager;
    private final UserChatRepository userChatMapRepository;
    private final ChatMapper chatMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final CustomRepository customRepository;
    private final UserChatRepository userChatRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public Page<MessageOutput> getMessages(String accessToken, Long chatId, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        eventNotificationRepository.deleteAllByUserIdAndChatId(userId, chatId);

        Page<MessageEntity> messageEntities = Filter.builder(MessageEntity.class, entityManager)
                .search()
                .isEqual("chatId1", chatId)
                .isEqual("chatId2", chatId)
                .isEqual("groupChatId", chatId)
                .orderBy("createdAt", Common.DESC)
                .getPage(pageable);

        List<Long> userIds = new ArrayList<>();
        ChatEntity chatEntity = customRepository.getChat(chatId);
        if (Common.USER.equals(chatEntity.getChatType())) {
            userIds.add(chatEntity.getUserId1());
            userIds.add(chatEntity.getUserId2());
        } else if (Common.GROUP.equals(chatEntity.getChatType())) {
            userIds.addAll(
                    userChatRepository.findAllByChatId(chatId).stream()
                            .map(UserChatMapEntity::getUserId)
                            .collect(Collectors.toList())
            );
        }
        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return messageEntities.map(
                messageEntity -> {
                    MessageOutput messageOutput = messageMapper.getOutputFromEntity(messageEntity);
                    if (userEntityMap.containsKey(messageEntity.getSenderId())) {
                        UserEntity userEntity = userEntityMap.get(messageEntity.getSenderId());
                        messageOutput.setUserId(userEntity.getId());
                        messageOutput.setFullName(userEntity.getFullName());
                        messageOutput.setImageUrl(userEntity.getImageUrl());
                        messageOutput.setIsMe(userId.equals(userEntity.getId()));
                    }
                    return messageOutput;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<ChatOutput> getChatList(String search, String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<Long> chatIds = null;
        List<UserChatMapEntity> userChatMapEntities = userChatMapRepository.findAllByUserId(userId);
        if (Objects.nonNull(userChatMapEntities) && !userChatMapEntities.isEmpty()) {
            chatIds = userChatMapEntities.stream()
                    .map(UserChatMapEntity::getChatId)
                    .collect(Collectors.toList());
        }

        Page<ChatEntity> chatEntities = Filter.builder(ChatEntity.class, entityManager)
                .search()
                .isIn("id", chatIds)
                .isEqual("userId1", userId)
                .filter()
                .isContain("name", search)
                .isNotNull("newestChatTime")
                .orderBy("newestChatTime", Common.DESC)
                .getPage(pageable);

        Map<Long, List<EventNotificationEntity>> eventNotificationMap =
                eventNotificationRepository.findAllByUserIdAndEventType(userId, Common.MESSAGE).stream()
                        .collect(Collectors.groupingBy(EventNotificationEntity::getChatId));

        return chatEntities.map(chatEntity -> {
            ChatOutput chatOutput = chatMapper.getOutputFromEntity(chatEntity);
            if (eventNotificationMap.containsKey(chatOutput.getId())) {
                chatOutput.setMessageCount(eventNotificationMap.get(chatOutput.getId()).size());
            } else {
                chatOutput.setMessageCount(0);
            }
            chatOutput.setIsMe(userId.equals(chatEntity.getNewestUserId()));
            return chatOutput;
        });
    }
}
