package com.example.Othellodifficult.dto.chat;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatAddNewMemberInput {
    private Long groupChatId;
    private List<Long> userIds;
}
