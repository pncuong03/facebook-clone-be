package com.example.Othellodifficult.dto.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventNotificationOutput {
    private Long id;
    private Long userId; // phuc = 2
    private String eventType; // message
    private String content;
}
