package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.message.MessageInput;
import com.example.Othellodifficult.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/message")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Gửi tin nhắn")
    @PostMapping
    public String send(@RequestBody MessageInput messageInput,  @RequestHeader("Authorization") String token){
        return messageService.send(messageInput, token);
    }
}
