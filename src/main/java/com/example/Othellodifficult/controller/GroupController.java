package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.service.GroupChatService;
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

}