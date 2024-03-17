package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.event.EventCountOutput;
import com.example.Othellodifficult.service.EventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/event-notification")
@AllArgsConstructor
public class EventNotificationController {
    private final EventNotificationService eventNotificationService;

    @GetMapping
    public EventCountOutput getEvents(@RequestHeader(value = "Authorization") String accessToken){
        return eventNotificationService.getEvent(accessToken);
    }
}
