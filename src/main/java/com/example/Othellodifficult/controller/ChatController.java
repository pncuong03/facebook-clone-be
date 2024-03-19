package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.chat.ChatOutput;
import com.example.Othellodifficult.dto.message.MessageOutput;
import com.example.Othellodifficult.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "Lấy danh sách tin nhắn trong cuộc trò chuyện")
    @GetMapping("/messages")
    public Page<MessageOutput> getMessages(@RequestHeader(Common.AUTHORIZATION) String accessToken,
                                           @RequestParam Long chatId,
                                           @ParameterObject Pageable pageable){
        return chatService.getMessages(accessToken, chatId, pageable);
    }

    @Operation(summary = "Lấy danh sách các cuộc chat của user")
    @GetMapping
    public Page<ChatOutput> getChatList(@RequestParam(required = false) String search,
                                        @RequestHeader(Common.AUTHORIZATION) String accessToken,
                                        @ParameterObject Pageable pageable){
        return chatService.getChatList(search, accessToken, pageable);
    }
}
