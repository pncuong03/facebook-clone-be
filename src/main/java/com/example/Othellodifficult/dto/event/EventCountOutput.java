package com.example.Othellodifficult.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventCountOutput {
    private int messageCount;
    private int informCount;
    private List<MessageEventOutput> messages;
}
