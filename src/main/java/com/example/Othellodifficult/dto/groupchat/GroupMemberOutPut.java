package com.example.Othellodifficult.dto.groupchat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupMemberOutPut {
    private Long id;
    private String username;
    private String image;
    private String positionInGroup;
}
