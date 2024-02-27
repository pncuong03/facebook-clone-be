package com.example.Othellodifficult.dto.chat;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatMemberOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
    private String role;
}
