package com.example.Othellodifficult.dto.post;

import com.example.Othellodifficult.common.Common;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostGroupInput {
    private String content;
    private String state = Common.PUBLIC;
    private Long groupId;
}
