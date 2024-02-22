package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.groupchat.*;
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
    public List<GroupChatMemberOutPut> getGroupMemBer(@PathVariable(value = "id") Long groupId){
        return groupChatService.getGroupMember(groupId);
    }
    @PostMapping("/add-new")
    public String addNewMember(@RequestBody GroupChatAddNewMemberInput groupChatAddNewMemberInput){
        return groupChatService.addNewMember(groupChatAddNewMemberInput);
    }
    @DeleteMapping("/delete-member")
    public String deleteMember(@RequestHeader("Authorization") String accessToken,
                               @RequestBody GroupChatDeleteMemberInput groupChatDeleteMemberInput)
    {
        return groupChatService.deleteMember(accessToken, groupChatDeleteMemberInput);
    }
    @DeleteMapping("/leave-group")
    public void leaveTheGroupChat(@RequestBody GroupChatLeaveTheGroupInput groupChatLeaveTheGroupInput){
        groupChatService.leaveTheGroupChat(groupChatLeaveTheGroupInput);
    }
}
