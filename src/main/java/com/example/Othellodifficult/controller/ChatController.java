package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.groupchat.*;
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
    public void create(@RequestBody GroupChatInput groupChatInput, @RequestHeader("Authorization") String accessToken){
        chatService.create(groupChatInput, accessToken);
    }
    @GetMapping("/{id}")
    public List<GroupChatMemberOutPut> getGroupMemBer(@PathVariable(value = "id") Long groupId){
        return chatService.getGroupChatMember(groupId);
    }
    @PostMapping("/add-new")
    public String addNewMember(@RequestBody GroupChatAddNewMemberInput groupChatAddNewMemberInput){
        return chatService.addNewMember(groupChatAddNewMemberInput);
    }
    @DeleteMapping("/delete-member")
    public String deleteMember(@RequestHeader("Authorization") String accessToken,
                               @RequestBody GroupChatDeleteMemberInput groupChatDeleteMemberInput)
    {
        return chatService.deleteMember(accessToken, groupChatDeleteMemberInput);
    }
    @DeleteMapping("/leave-group")
    public void leaveTheGroupChat(@RequestBody GroupChatLeaveTheGroupInput groupChatLeaveTheGroupInput){
        chatService.leaveTheGroupChat(groupChatLeaveTheGroupInput);
    }
}
