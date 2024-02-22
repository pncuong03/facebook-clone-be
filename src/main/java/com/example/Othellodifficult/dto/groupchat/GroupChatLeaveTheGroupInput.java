package com.example.Othellodifficult.dto.groupchat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupChatLeaveTheGroupInput {
    private Long groupId;
    private Long userId;
}
