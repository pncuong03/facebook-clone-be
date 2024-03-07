package com.example.Othellodifficult.dto.message;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageInput {
    @NonNull
    private Long chatId;
    @NotBlank
    private String message;
}
