package com.example.Othellodifficult.dto.friends;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListFriendOutput {
    private Long id;
    private Long userId;
    private String name;
    private String image;
}
