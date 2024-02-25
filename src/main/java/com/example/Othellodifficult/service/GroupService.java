package com.example.Othellodifficult.service;
import com.example.Othellodifficult.dto.group.*;
import com.example.Othellodifficult.entity.GroupEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupEntity;
import com.example.Othellodifficult.repository.GroupRepository;
import com.example.Othellodifficult.repository.UserGroupRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    public void create(GroupInput groupInput, String token){
        Long manager_user_id = TokenHelper.getUserIdFromToken(token);
        GroupEntity groupEntity = GroupEntity.builder()
                .name(groupInput.getName())
                .manager_group_id(manager_user_id)
                .build();
        groupRepository.save(groupEntity);
        userGroupRepository.save(UserGroupEntity.builder()
                .userId(manager_user_id)
                .groupId(groupEntity.getId())
                .build());
        for(Long userId:groupInput.getListUserId()){
            userGroupRepository.save(UserGroupEntity.builder()
                    .userId(userId)
                    .groupId(groupEntity.getId())
                    .build());
        }

    }
    public List<GroupMemberOutPut> getGroupMember(Long groupId) {
        // get  managerId
        List<UserGroupEntity> listUserGroupEntity = userGroupRepository.findAllByGroupId(groupId);
        Long managerId = groupRepository.findById(groupId).get().getManager_group_id();
        // get infor member
        List<UserEntity> listUserEntity = new ArrayList<>();
        for (UserGroupEntity user : listUserGroupEntity) {
            UserEntity userEntity = userRepository.findById(user.getUserId()).get();
            listUserEntity.add(userEntity);
        }
        // map to output
        List<GroupMemberOutPut> groupMemberOutPutList = new ArrayList<>();
        for (UserEntity user : listUserEntity) {
            if (user.getId() == managerId) {
                groupMemberOutPutList.add(
                        GroupMemberOutPut.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .image(null)
                                .positionInGroup("Admin")
                                .build()
                );
            } else {
                groupMemberOutPutList.add(
                        GroupMemberOutPut.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .image(null)
                                .positionInGroup("MemBer")
                                .build()
                );
            }

        }
        return groupMemberOutPutList;
    }

    public String addNewMember(GroupAddNewMemberInput groupAddNewMemberInput) {

        List<UserGroupEntity> listUserGroupEntity = userGroupRepository
                .findAllByGroupId(
                        groupAddNewMemberInput.getGroupId()
                );
        List<Long> listUserIdExitInGroup = new ArrayList<>();
        for (UserGroupEntity userGroup : listUserGroupEntity) {
            listUserIdExitInGroup.add(userRepository.findById(
                    userGroup.getUserId()
            ).get().getId());
        }
        for (Long newUserId : groupAddNewMemberInput.getListUserId()) {
            if (!listUserIdExitInGroup.contains(newUserId)) {
                userGroupRepository.save(
                        UserGroupEntity.builder()
                                .groupId(groupAddNewMemberInput.getGroupId())
                                .userId(newUserId)
                                .build()
                );
            } else {
                return "The User is already in Group";

            }
        }
        return "Add new Users successfull";
        //
    }
    @Transactional

    public String deleteMember(String token, GroupDeleteMemberInput groupDeleteMemberInput) {
        Long CheckUserId = TokenHelper.getUserIdFromToken(token);
        Long managerId = groupRepository
                .findById(groupDeleteMemberInput.getGroupId())
                .get().getManager_group_id();
        if (CheckUserId == managerId) {
            Long userDeleteId = groupDeleteMemberInput.getUserId();
            if (userDeleteId != managerId) {
                userGroupRepository.deleteByUserIdAndGroupId(userDeleteId,
                        groupDeleteMemberInput.getGroupId());
                return "Success";
            } else {
                return "You can't delete YourSelf";
            }
        }
        return "You can't delete the others people";

    }
    @Transactional
    public void leaveTheGroup(GroupLeaveTheGroupInput groupLeaveTheGroupInput) {
         /* Need to check the position of managerGroup, if the manager leave,
        he will need to transfer his role to another
     */
        if (userGroupRepository.countByGroupId(
                groupLeaveTheGroupInput.getGroupId()) > 1) // check the number of row in table UserGroupChat
        {
            userGroupRepository.deleteByUserIdAndGroupId(
                    groupLeaveTheGroupInput.getUserId(),
                    groupLeaveTheGroupInput.getGroupId()
            );
        } else {
            // delte the last member in UserGroupChat table and delete immidiately table GroupChat with groupId
            userGroupRepository.deleteByUserIdAndGroupId(
                    groupLeaveTheGroupInput.getUserId(),
                    groupLeaveTheGroupInput.getGroupId()
            );
            groupRepository.deleteById(groupLeaveTheGroupInput.getGroupId());
        }

    }
}
