package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.event.EventCountOutput;
import com.example.Othellodifficult.service.EventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/event-notification")
@AllArgsConstructor
@CrossOrigin
public class EventNotificationController {
    private final EventNotificationService eventNotificationService;

    @DeleteMapping
    public void deleteMessageEvent(@RequestHeader(value = "Authorization") String accessToken,
                                   @RequestParam Long chatId){
        eventNotificationService.deleteMessageEvent(accessToken, chatId);
    }

    @GetMapping
    public EventCountOutput getEvents(@RequestHeader(value = "Authorization") String accessToken,
                                      @RequestParam(required = false) Long chatId){
        return eventNotificationService.getEvent(accessToken, chatId);
    }
}
