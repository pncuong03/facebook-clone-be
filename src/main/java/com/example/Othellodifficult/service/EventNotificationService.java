package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.event.EventNotificationOutput;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.UserChatEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventNotificationService {
    private final EventNotificationRepository eventNotificationRepository;

    @Transactional
    public List<EventNotificationOutput> getEvent(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        while (Boolean.TRUE.equals(Boolean.TRUE)) {
            List<EventNotificationEntity> eventNotificationEntities = eventNotificationRepository.findAllByUserId(userId);

            if (Objects.nonNull(eventNotificationEntities) && !eventNotificationEntities.isEmpty()) {
                List<EventNotificationOutput> eventNotificationOutputs = new ArrayList<>();
                for (EventNotificationEntity eventNotificationEntity : eventNotificationEntities) {
                    eventNotificationOutputs.add(
                            EventNotificationOutput.builder()
                                    .id(eventNotificationEntity.getId())
                                    .eventType(eventNotificationEntity.getEventType())
                                    .content("comming soon !!!")
                                    .build()
                    );
                }
                CompletableFuture.runAsync(() -> {
                    eventNotificationRepository.deleteAll(eventNotificationEntities);
                });
                return eventNotificationOutputs;
            }
        }


        return null;
    }
}
