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
    private String message;
    private LocalDateTime createdAt;
}
