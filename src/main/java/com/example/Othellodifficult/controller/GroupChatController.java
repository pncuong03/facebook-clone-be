package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.groupchat.GroupChatInput;
import com.example.Othellodifficult.dto.groupchat.GroupMemberOutPut;
import com.example.Othellodifficult.service.GroupChatService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/group-chat")
@AllArgsConstructor
public class GroupChatController {
    private final GroupChatService groupChatService;
    @PostMapping
    public void create(@RequestBody GroupChatInput groupChatInput, @RequestHeader("Authorization") String accessToken){
        groupChatService.create(groupChatInput, accessToken);
    }
    @GetMapping("/{id}")
    public List<GroupMemberOutPut> getGroupMemBer(@PathVariable(value = "id") Long groupId){
        return groupChatService.getGroupMember(groupId);
    }
}
