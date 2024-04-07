package com.example.Othellodifficult.dto.friends;

import lombok.*;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FriendInforOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
    private String backgroundUrl;
    private String description;
    private Long chatId;
    private String state; // FRIEND/ STRANGER/ REQUESTING
}
