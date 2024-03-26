package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/group")
@AllArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @Operation(summary = "Tạo nhóm")
    @PostMapping("/create-group")
    public void create(@RequestBody @Valid GroupInput groupInput,
                       @RequestHeader("Authorization") String accessToken) {
        groupService.create(groupInput, accessToken);
    }

    @Operation(summary = "Tìm kiếm nhóm")
    @GetMapping("/search")
    public Page<GroupOutput> getGroups(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) Long tagId,
                                       @ParameterObject Pageable pageable) {
        return groupService.getGroups(search, tagId, pageable);
    }

    @Operation(summary = "Lấy danh sách thành viên trong nhóm")
    @GetMapping("/members") //
    public Page<GroupMemberOutPut> getGroupMemBer(@RequestParam Long groupId,
                                                  @RequestHeader("Authorization") String accessToken,
                                                  @ParameterObject Pageable pageable) {
        return groupService.getGroupMembers(groupId, accessToken, pageable);
    }

    @Operation(summary = "Thêm thành viên vào nhóm")
    @PostMapping("/add-member")
    public void addNewMember(@RequestBody @Valid GroupAddNewMemberInput groupAddNewMemberInput,
                             @RequestHeader("Authorization") String accessToken) {
        groupService.addNewMember(groupAddNewMemberInput, accessToken);
    }

    @Operation(summary = "Xóa thành viên khỏi nhóm")
    @DeleteMapping("/delete-member")
    public void deleteMember(@RequestHeader("Authorization") String accessToken,
                             @RequestBody @Valid GroupDeleteMemberInput groupDeleteMemberInput) {
        groupService.deleteMember(accessToken, groupDeleteMemberInput);
    }

    @Operation(summary = "Rời nhóm")
    @DeleteMapping("/leave-group")
    public void leaveTheGroupChat(@RequestHeader("Authorization") String accessToken,
                                  @RequestParam Long groupId) {
        groupService.leaveTheGroup(accessToken,groupId);
    }
}