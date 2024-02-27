package com.example.Othellodifficult.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupChatInput {
    @NotEmpty
    private String name;
    @NotEmpty
    private List<Long> userIds;
}
