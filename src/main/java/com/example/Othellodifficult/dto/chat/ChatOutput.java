package com.example.Othellodifficult.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatOutput {
    private Long id;
    private String name;
    private String imageUrl;
    private String newestMessage;
    private Boolean isMe;
    private LocalDateTime newestChatTime;
    private Integer messageCount;
}
