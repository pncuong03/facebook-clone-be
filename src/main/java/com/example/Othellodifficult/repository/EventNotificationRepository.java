package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.entity.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNotificationRepository extends JpaRepository<EventNotificationEntity, Long> {
    List<EventNotificationEntity> findAllByUserId(Long userId);
}
