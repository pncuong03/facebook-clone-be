package com.example.Othellodifficult.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeInfoUserRequest {
    private String fullName;
    private String birthdayString;
    private String gender;
}
