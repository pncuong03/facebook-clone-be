package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.entity.GroupEntity;
import com.example.Othellodifficult.entity.GroupTagMapEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupMapEntity;
import com.example.Othellodifficult.mapper.GroupMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupMapRepository userGroupMapRepository;
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
                    // kiem tra ham finAllByIdIn(groupId,pageable) co chuyen thanh findAllByTagIdIn
                    Arrays.asList(customRepository.getTag(tagId).getId()), pageable
            );
        }
        return groupEntities.map(groupMapper::getOutputFromEntity);
    }

    @Transactional
    public void create(GroupInput groupInput, String accessToken) {
        Long managerId = TokenHelper.getUserIdFromToken(accessToken);
        GroupEntity groupEntity = GroupEntity.builder()
                .name(groupInput.getName())
                .memberCount(groupInput.getUserIds().size() +1)
                .build();
        groupRepository.save(groupEntity);
        userGroupMapRepository.save(
                UserGroupMapEntity.builder()
                        .userId(managerId)
                        .groupId(groupEntity.getId())
                        .role(Common.ADMIN)
                        .build()
        );

        for (Long userId : groupInput.getUserIds()) {
            if(!managerId.equals(userId)){
                userGroupMapRepository.save(
                        UserGroupMapEntity.builder()
                                .userId(userId)
                                .role(Common.MEMBER)
                                .groupId(groupEntity.getId())
                                .build()
                );
            }
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
        if (Boolean.FALSE.equals(userGroupMapRepository.existsByUserIdAndGroupId(
                TokenHelper.getUserIdFromToken(accessToken), groupId
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        Page<UserGroupMapEntity> userGroupEntities = userGroupMapRepository.findAllByGroupId(groupId, pageable);
        if (Objects.isNull(userGroupEntities) || userGroupEntities.isEmpty()) {
            return Page.empty();
        }
        Long managerId = userGroupMapRepository.findByGroupIdAndRole(groupId,Common.ADMIN).getUserId();

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                userGroupEntities.stream().map(UserGroupMapEntity::getUserId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return userGroupEntities.map(
                userGroupEntity -> {
                    UserEntity userEntity = userEntityMap.get(userGroupEntity.getUserId());
                    return GroupMemberOutPut.builder()
                            .id(userEntity.getId())
                            .fullName(userEntity.getFullName())
                            .imageUrl(userEntity.getImageUrl())
                            .role(userEntity.getId().equals(managerId) ? Common.ADMIN : Common.MEMBER)
                            .build();
                }
        );
    }

    @Transactional
    public void addNewMember(GroupAddNewMemberInput groupAddNewMemberInput, String accessToken) {
        if (Boolean.FALSE.equals(userGroupMapRepository.existsByUserIdInAndGroupId(
                Arrays.asList(TokenHelper.getUserIdFromToken(accessToken)), groupAddNewMemberInput.getGroupId()
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        if (Boolean.TRUE.equals(userGroupMapRepository.existsByUserIdInAndGroupId(
                groupAddNewMemberInput.getUserIds(), groupAddNewMemberInput.getGroupId()
        ))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        for (Long newUserId : groupAddNewMemberInput.getUserIds()) {
            userGroupMapRepository.save(
                    UserGroupMapEntity.builder()
                            .userId(newUserId)
                            .groupId(groupAddNewMemberInput.getGroupId())
                            .build()
            );
        }
    }

    @Transactional
    public void deleteMember(String accessToken, GroupDeleteMemberInput groupDeleteMemberInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Long managerId = userGroupMapRepository
                .findByGroupIdAndRole(groupDeleteMemberInput.getGroupId(),Common.ADMIN).getUserId();
        if (!userId.equals(managerId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        if (userId.equals(groupDeleteMemberInput.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        userGroupMapRepository.deleteByUserIdAndGroupId(groupDeleteMemberInput.getUserId(), groupDeleteMemberInput.getGroupId());
    }

    @Transactional
    public void leaveTheGroup(String accessToken, Long groupId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (userGroupMapRepository.countByGroupId(groupId) > 1) {
            userGroupMapRepository.deleteByUserIdAndGroupId(userId,groupId);
        } else {
            userGroupMapRepository.deleteByUserIdAndGroupId(userId,groupId);
            groupRepository.deleteById(groupId);
        }
    }
}
