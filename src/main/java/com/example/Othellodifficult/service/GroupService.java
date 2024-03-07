package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.entity.GroupEntity;
import com.example.Othellodifficult.entity.GroupTagMapEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupEntity;
import com.example.Othellodifficult.mapper.GroupMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.HTML;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final CustomRepository customRepository;
    private final GroupTagMapRepository groupTagMapRepository;
    private final GroupMapper groupMapper;

    @Transactional(readOnly = true)
    public Page<GroupOutput> getGroups(String search, Long tagId, Pageable pageable){
        Page<GroupEntity> groupEntities = Page.empty();
        if (Objects.isNull(search) && Objects.isNull(tagId)){
            groupEntities = groupRepository.findAll(pageable);
        } else if (Objects.nonNull(search)) {
            groupEntities = groupRepository.findAllByNameContainsIgnoreCase(search, pageable);
        }
        else {
            groupEntities = groupRepository.findAllByIdIn(
                    Arrays.asList(customRepository.getTag(tagId).getId()), pageable
            );
        }
        return groupEntities.map(groupMapper::getOutputFromEntity);
    }

    @Transactional
    public void create(GroupInput groupInput, String accessToken) {
        Long managerUserId = TokenHelper.getUserIdFromToken(accessToken);
        GroupEntity groupEntity = GroupEntity.builder()
                .name(groupInput.getName())
                .managerGroupId(managerUserId)
                .memberCount(groupInput.getUserIds().size())
                .build();
        groupRepository.save(groupEntity);
        userGroupRepository.save(UserGroupEntity.builder()
                .userId(managerUserId)
                .groupId(groupEntity.getId())
                .build());
        for (Long userId : groupInput.getUserIds()) {
            userGroupRepository.save(
                    UserGroupEntity.builder()
                            .userId(userId)
                            .groupId(groupEntity.getId())
                            .build()
            );
        }
        for (Long tagId : groupInput.getTagIds()) {
            groupTagMapRepository.save(
                    GroupTagMapEntity.builder()
                            .tagId(tagId)
                            .groupId(groupEntity.getId())
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    public Page<GroupMemberOutPut> getGroupMembers(Long groupId, String accessToken, Pageable pageable) {
        if (Boolean.FALSE.equals(userGroupRepository.existsByUserIdAndGroupId(
                TokenHelper.getUserIdFromToken(accessToken), groupId
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        Page<UserGroupEntity> userGroupEntities = userGroupRepository.findAllByGroupId(groupId, pageable);
        if (Objects.isNull(userGroupEntities) || userGroupEntities.isEmpty()) {
            return Page.empty();
        }
        Long managerId = customRepository.getGroup(groupId).getManagerGroupId();

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                userGroupEntities.stream().map(UserGroupEntity::getUserId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return userGroupEntities.map(
                userGroupEntity -> {
                    UserEntity userEntity = userEntityMap.get(userGroupEntity.getUserId());
                    return GroupMemberOutPut.builder()
                            .id(userEntity.getId())
                            .fullName(userEntity.getFullName())
                            .imageUrl(userEntity.getImageUrl())
                            .positionInGroup(userEntity.getId().equals(managerId) ? Common.ADMIN : Common.MEMBER)
                            .build();
                }
        );
    }

    @Transactional
    public void addNewMember(GroupAddNewMemberInput groupAddNewMemberInput, String accessToken) {
        if (Boolean.FALSE.equals(userGroupRepository.existsByUserIdInAndGroupId(
                Arrays.asList(TokenHelper.getUserIdFromToken(accessToken)), groupAddNewMemberInput.getGroupId()
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        if (Boolean.TRUE.equals(userGroupRepository.existsByUserIdInAndGroupId(
                groupAddNewMemberInput.getUserIds(), groupAddNewMemberInput.getGroupId()
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        for (Long newUserId : groupAddNewMemberInput.getUserIds()) {
            userGroupRepository.save(
                    UserGroupEntity.builder()
                            .userId(newUserId)
                            .groupId(groupAddNewMemberInput.getGroupId())
                            .build()
            );
        }
    }

    @Transactional
    public void deleteMember(String accessToken, GroupDeleteMemberInput groupDeleteMemberInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Long managerId = customRepository.getGroup(groupDeleteMemberInput.getGroupId()).getManagerGroupId();
        if (!userId.equals(managerId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        if (userId.equals(groupDeleteMemberInput.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        userGroupRepository.deleteByUserIdAndGroupId(groupDeleteMemberInput.getUserId(), groupDeleteMemberInput.getGroupId());
    }

    @Transactional
    public void leaveTheGroup(GroupLeaveTheGroupInput groupLeaveTheGroupInput) {
        if (userGroupRepository.countByGroupId(groupLeaveTheGroupInput.getGroupId()) > 1) {
            userGroupRepository.deleteByUserIdAndGroupId(
                    groupLeaveTheGroupInput.getUserId(),
                    groupLeaveTheGroupInput.getGroupId()
            );
        } else {
            userGroupRepository.deleteByUserIdAndGroupId(
                    groupLeaveTheGroupInput.getUserId(),
                    groupLeaveTheGroupInput.getGroupId()
            );
            groupRepository.deleteById(groupLeaveTheGroupInput.getGroupId());
        }
    }
}
