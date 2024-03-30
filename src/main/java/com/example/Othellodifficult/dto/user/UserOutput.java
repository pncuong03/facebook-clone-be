package com.example.Othellodifficult.dto.user;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
}
