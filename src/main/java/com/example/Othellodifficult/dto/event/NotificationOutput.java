package com.example.Othellodifficult.dto.event;

import com.example.Othellodifficult.dto.IdAndName;
import com.example.Othellodifficult.dto.user.UserOutput;
import com.example.Othellodifficult.dto.user.UserOutputV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationOutput {
    private Long id;
    private String type; // group/ user
    private Long userId; // của mình
    private Long interactId; // người tương tác với mình
    private Long groupId; // nhóm tương tác
    private String interactType; // Like, share, comment,
    private Long postId;
    private Boolean hasSeen; // false
    private LocalDateTime createdAt;

    private UserOutput interact;
    private IdAndName group;
}
