package com.example.Othellodifficult.dto.group;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupLeaveTheGroupInput {
    @NonNull
    private Long groupId;
    @NonNull
    private Long userId;
}
