package com.example.Othellodifficult.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEventOutput {
    private Long chatId;
    private Long userId;
    private String fullName;
    private String imageUrl;
    private String message;
    private Boolean isMe;
    private LocalDateTime createdAt;
}
