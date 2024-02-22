package com.example.Othellodifficult.dto.group;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupInput {
    private String name;
    private List<Long> listUserId;
}
