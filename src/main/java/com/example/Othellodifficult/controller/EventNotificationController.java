package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.event.EventNotificationOutput;
import com.example.Othellodifficult.service.EventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/event-notification")
@AllArgsConstructor
public class EventNotificationController {
    private final EventNotificationService eventNotificationService;

    @GetMapping
    public List<EventNotificationOutput> getEvent(@RequestHeader(value = "Authorization") String accessToken){
        return eventNotificationService.getEvent(accessToken);
    }
}
