package com.example.Othellodifficult.dto.groupchat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatInput {
    private String name;
    private List<Long> userId;
}
