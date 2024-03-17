package com.example.Othellodifficult.entity.message;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_event_notification")
public class EventNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String eventType; // MESSAGE; NOTIFICATION
    private String state; // NEW_EVENT; OLD_EVENT
    private Long chatId;
    private String message;
    private LocalDateTime createdAt;
}
