package com.example.Othellodifficult.dto.chat;

import com.example.Othellodifficult.common.Common;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroupChatOutPut {
    private Long id;
    private String name;
//    private String img = Common.DEFAULT_IMAGE_URL;
}
