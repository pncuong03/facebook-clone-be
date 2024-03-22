package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.event.EventCountOutput;
import com.example.Othellodifficult.dto.event.MessageEventOutput;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.mapper.NotificationMapper;
import com.example.Othellodifficult.repository.EventNotificationRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventNotificationService {
    private final EventNotificationRepository eventNotificationRepository;
    // https://community.sonarsource.com/t/java-rule-s3077-should-not-apply-to-references-to-immutable-objects/15200/3
    // https://community.sonarsource.com/t/non-primitive-fields-should-not-be-volatile-spurious-bug/89068
    public static volatile Map<Long, Integer> map1 = new HashMap<>();  // currentNewMessage
    public static volatile Map<Long, Integer> map2 = new HashMap<>();  // oldNewMessage
    private final NotificationMapper notificationMapper;

    @Transactional
    public EventCountOutput getEvent(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (!map1.containsKey(userId)){
            System.out.println("FIRST CONNECT OF USER " + userId);
            List<EventNotificationEntity> eventNotificationEntities = eventNotificationRepository.findAllByUserId(userId);
            map1.put(userId, eventNotificationEntities.size());
            map2.put(userId,
                    eventNotificationEntities.stream()
                            .filter(e -> e.getState().equals(Common.OLD_EVENT))
                            .collect(Collectors.toList()).size()
            );
        }

        System.out.println("MAP HAVE:");
        System.out.println(map1);
        System.out.println(map2);

        while (true) {
            if (!map1.get(userId).equals(map2.get(userId))) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(Common.ACTION_FAIL);
                }
                System.out.println("NEW EVENT FOR USER_ID :" + userId);
                System.out.println("MAP HAVE:");
                System.out.println(map1);
                System.out.println(map2);
                map2.put(userId, map1.get(userId));
                System.out.println("MAP 2 AFTER PUSH: " + map2.get(userId));
                List<EventNotificationEntity> events = eventNotificationRepository.findAllByUserId(userId);

                List<EventNotificationEntity> newEvents = new ArrayList<>();
                for (EventNotificationEntity event : events) {
                    if (Common.NEW_EVENT.equals(event.getState())) {
                        newEvents.add(event);
                    }
                }
                Set<Long> messagesForCount = new HashSet<>();
                List<MessageEventOutput> messageEventOutputs = new ArrayList<>();
                if (!newEvents.isEmpty()) {
                    EventCountOutput eventCountOutput = new EventCountOutput();
                    for (EventNotificationEntity event : events) {
                        if (Common.MESSAGE.equals(event.getEventType()) && Common.NEW_EVENT.equals(event.getState())) {
                            messagesForCount.add(event.getChatId());
                            messageEventOutputs.add(notificationMapper.getOutputFromEntity(event));
                        } else if (Common.NOTIFICATION.equals(event.getEventType())) {
                            eventCountOutput.setInformCount(eventCountOutput.getInformCount() + 1);
                        }
                    }
                    for (EventNotificationEntity newEvent : newEvents) {
                        if (Common.NOTIFICATION.equals(newEvent.getEventType())){
                            newEvent.setState(Common.OLD_EVENT);
                        }
                    }
                    eventCountOutput.setMessageCount(messagesForCount.size());
                    if (!messageEventOutputs.isEmpty()){
                        eventCountOutput.setMessages(
                                messageEventOutputs.stream()
                                        .sorted(Comparator.comparing(MessageEventOutput::getCreatedAt))
                                        .collect(Collectors.toList())
                        );
                    }
                    eventNotificationRepository.saveAll(newEvents);
                    return eventCountOutput;
                }
            }
        }
    }
}
