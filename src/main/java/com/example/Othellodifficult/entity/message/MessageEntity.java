package com.example.Othellodifficult.entity.message;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tbl_message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long chatId1;
    private Long chatId2;
    private Long groupChatId;
    private String message;
    private LocalDateTime createdAt;
}
