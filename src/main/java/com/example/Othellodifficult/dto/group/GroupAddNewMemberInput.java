package com.example.Othellodifficult.dto.group;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupAddNewMemberInput {
    private Long groupId;
    private List<Long> listUserId;
}
