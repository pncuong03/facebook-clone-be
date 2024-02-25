package com.example.Othellodifficult.dto.chat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatLeaveTheGroupInput {
    private Long groupId;
    private Long userId;
}
