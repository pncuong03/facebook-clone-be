package com.example.Othellodifficult.dto.groupchat;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChatAddNewMemberInput {
    private Long groupId;
    private List<Long> listUserId;
}
