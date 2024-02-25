package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.chat.*;
import com.example.Othellodifficult.service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/group-chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @PostMapping
    public void create(@RequestBody ChatInput chatInput, @RequestHeader(value = "Authorization") String accessToken){
        chatService.create(chatInput, accessToken);
    }
    @GetMapping("/{id}")
    public List<ChatMemberOutput> getGroupMemBer(@PathVariable(value = "id") Long groupId){
        return chatService.getGroupChatMember(groupId);
    }
    @PostMapping("/add-new")
    public String addNewMember(@RequestBody ChatAddNewMemberInput chatAddNewMemberInput){
        return chatService.addNewMember(chatAddNewMemberInput);
    }
    @DeleteMapping("/delete-member")
    public String deleteMember(@RequestHeader("Authorization") String accessToken,
                               @RequestBody ChatDeleteMemberInput chatDeleteMemberInput)
    {
        return chatService.deleteMember(accessToken, chatDeleteMemberInput);
    }
    @DeleteMapping("/leave-group")
    public void leaveTheGroupChat(@RequestBody ChatLeaveTheGroupInput chatLeaveTheGroupInput){
        chatService.leaveTheGroupChat(chatLeaveTheGroupInput);
    }
}
