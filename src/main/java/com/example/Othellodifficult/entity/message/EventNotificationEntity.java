package com.example.Othellodifficult.entity.message;

import lombok.*;

import javax.persistence.*;

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
    private String eventType; // MESSAGE; FRIEND_REQUEST
    private String state;
}
