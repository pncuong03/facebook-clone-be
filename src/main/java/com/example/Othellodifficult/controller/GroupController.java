package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.dto.groupchat.GroupChatAddNewMemberInput;
import com.example.Othellodifficult.dto.groupchat.GroupChatDeleteMemberInput;
import com.example.Othellodifficult.dto.groupchat.GroupChatLeaveTheGroupInput;
import com.example.Othellodifficult.dto.groupchat.GroupChatMemberOutPut;
import com.example.Othellodifficult.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;
    @PostMapping
    public void create(@RequestBody GroupInput groupInput, @RequestHeader("Authorization") String accessToken){
        groupService.create(groupInput, accessToken);
    }
    @GetMapping("/{id}")
    public List<GroupMemberOutPut> getGroupMemBer(@PathVariable(value = "id") Long groupId){
        return groupService.getGroupMember(groupId);
    }
    @PostMapping("/add-new")
    public String addNewMember(@RequestBody GroupAddNewMemberInput groupAddNewMemberInput){
        return groupService.addNewMember(groupAddNewMemberInput);
    }
    @DeleteMapping("/delete-member")
    public String deleteMember(@RequestHeader("Authorization") String accessToken,
                               @RequestBody GroupDeleteMemberInput groupDeleteMemberInput)
    {
        return groupService.deleteMember(accessToken, groupDeleteMemberInput);
    }
    @DeleteMapping("/leave-group")
    public void leaveTheGroupChat(@RequestBody GroupLeaveTheGroupInput groupLeaveTheGroupInput){
        groupService.leaveTheGroup(groupLeaveTheGroupInput);
    }
}