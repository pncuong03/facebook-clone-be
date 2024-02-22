package com.example.Othellodifficult.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupLeaveTheGroupInput {
    private Long groupId;
    private Long userId;
}
