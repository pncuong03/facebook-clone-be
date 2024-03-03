package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.event.EventCountOutput;
import com.example.Othellodifficult.dto.event.EventNotificationOutput;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class EventNotificationService {
    private final EventNotificationRepository eventNotificationRepository;

    @Transactional
    public EventCountOutput getEvent(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        while (Boolean.TRUE.equals(Boolean.TRUE)) {
            List<EventNotificationEntity> events = eventNotificationRepository.findAllByUserId(userId);

            List<EventNotificationEntity> newEvents = new ArrayList<>();
            for (EventNotificationEntity event : events){
                if (Common.NEW_EVENT.equals(event.getState())){
                    newEvents.add(event);
                }
            }

            if (!newEvents.isEmpty()) {
                EventCountOutput eventCountOutput = new EventCountOutput();
                for (EventNotificationEntity event : events){
                    if (Common.MESSAGE.equals(event.getEventType())){
                        eventCountOutput.setMessageCount(eventCountOutput.getMessageCount() + 1);
                    }
                    else {
                        eventCountOutput.setInformCount(eventCountOutput.getInformCount() + 1);
                    }
                }
                for (EventNotificationEntity newEvent : newEvents){
                    newEvent.setState(Common.OLD_EVENT);
                }
                eventNotificationRepository.saveAll(newEvents);
                return eventCountOutput;
            }
        }
        return null;
    }
}
