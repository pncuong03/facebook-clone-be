package com.example.Othellodifficult.dto.friends;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
}
