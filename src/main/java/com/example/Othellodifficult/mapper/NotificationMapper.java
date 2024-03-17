package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.event.MessageEventOutput;
import com.example.Othellodifficult.dto.event.NotificationOutput;
import com.example.Othellodifficult.entity.NotificationEntity;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {
    NotificationOutput getOutputFromEntity(NotificationEntity notificationEntity);
    MessageEventOutput getOutputFromEntity(EventNotificationEntity entity);
}

