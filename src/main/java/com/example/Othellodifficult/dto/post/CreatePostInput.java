package com.example.Othellodifficult.dto.post;

import lombok.*;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostInput {
    private String content;
    @Pattern(regexp = "^(PRIVATE|PUBLIC)")
    private String state;
}
