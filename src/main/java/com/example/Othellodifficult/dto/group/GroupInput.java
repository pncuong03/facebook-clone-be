package com.example.Othellodifficult.dto.group;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupInput {
    @NotBlank
    private String name;
    @Size(min = 1)
    private List<Long> userIds;
    @Size(min = 1)
    private List<Long> tagIds; // db : tiếng anh, game, tiếng nhật ...
}
