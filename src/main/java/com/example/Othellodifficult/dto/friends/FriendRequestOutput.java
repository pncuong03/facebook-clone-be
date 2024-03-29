package com.example.Othellodifficult.dto.friends;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
    private LocalDateTime createdAt;
}
