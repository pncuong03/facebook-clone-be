package com.example.Othellodifficult.dto.friends;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendPerPageOutput {
    private Long id;
    private Long userId;
    private String name;
    private String image;
}
