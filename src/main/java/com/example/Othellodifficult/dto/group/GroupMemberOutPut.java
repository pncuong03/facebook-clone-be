package com.example.Othellodifficult.dto.group;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberOutPut {
    private Long id;
    private String username;
    private String image;
    private String positionInGroup;
}
