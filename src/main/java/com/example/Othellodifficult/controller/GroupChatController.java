package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.chat.*;
import com.example.Othellodifficult.service.GroupChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/group-chat")
@AllArgsConstructor
public class GroupChatController {
    private final GroupChatService chatService;

    @Operation(summary = "Tạo nhóm chat")
    @PostMapping
    public void createGroupChat(@RequestBody @Valid CreateGroupChatInput chatInput,
                                @RequestHeader(value = "Authorization") String accessToken) {
        chatService.createGroupChat(chatInput, accessToken);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách thành viên nhóm chat")
    public List<ChatMemberOutput> getGroupMemBersBy(@RequestParam Long groupId,
                                                    @RequestHeader(value = "Authorization") String accessToken) {
        return chatService.getGroupChatMembersBy(groupId, accessToken);
    }

    @PostMapping("/add-new")
    @Operation(summary = "Thêm thành viên vào nhóm chat")
    public void addNewMemberToGroupChat(@RequestBody ChatAddNewMemberInput chatAddNewMemberInput,
                                        @RequestHeader(value = "Authorization") String accessToken) {
        chatService.addNewMemberToGroupChat(chatAddNewMemberInput, accessToken);
    }

    @DeleteMapping("/delete-member")
    @Operation(summary = "Xóa thành viên khỏi nhóm chat")
    public void deleteMember(@RequestHeader("Authorization") String accessToken,
                             @RequestBody ChatDeleteMemberInput chatDeleteMemberInput) {
        chatService.deleteMember(accessToken, chatDeleteMemberInput);
    }

    @DeleteMapping("/leave-group")
    @Operation(summary = "Rời nhóm chat")
    public void leaveTheGroupChat(@RequestBody ChatLeaveTheGroupInput chatLeaveTheGroupInput) {
        chatService.leaveTheGroupChat(chatLeaveTheGroupInput);
    }
}
