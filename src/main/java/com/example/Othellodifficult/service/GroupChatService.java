package com.example.Othellodifficult.service;

import com.example.Othellodifficult.dto.groupchat.*;
import com.example.Othellodifficult.entity.GroupChatEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupChatEntity;
import com.example.Othellodifficult.mapper.GroupChatMapper;
import com.example.Othellodifficult.repository.GroupChatRepository;
import com.example.Othellodifficult.repository.UserGroupChatRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupChatService {
    private final GroupChatMapper groupChatMapper;
    private final GroupChatRepository groupChatRepository;
    private final UserGroupChatRepository userGroupChatRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(GroupChatInput groupChatInput, String token) {
        Long managerId = TokenHelper.getUserIdFromToken(token);
        GroupChatEntity groupChatEntity = groupChatMapper.getEntityFromInput(groupChatInput);

        groupChatEntity.setManagerId(managerId);

        groupChatRepository.save(groupChatEntity);

        userGroupChatRepository.save(
                UserGroupChatEntity.builder()
                        .userId(managerId)
                        .groupId(groupChatEntity.getId())
                        .build()
        );

        for (Long userId : groupChatInput.getUserId()) {
            userGroupChatRepository.save(
                    UserGroupChatEntity.builder()
                            .userId(userId)
                            .groupId(groupChatEntity.getId())
                            .build()
            );
        }

    }

    public List<GroupChatMemberOutPut> getGroupChatMember(Long groupId) {
        // get  managerId
        List<UserGroupChatEntity> listUserGroupChatEntity = userGroupChatRepository.findAllByGroupId(groupId);
        GroupChatEntity groupChatEntity = groupChatRepository.findById(groupId).get();
        Long managerId = groupChatEntity.getManagerId();
        // get infor member
        List<UserEntity> listUserEntity = new ArrayList<>();
        for (UserGroupChatEntity user : listUserGroupChatEntity) {
            UserEntity userEntity = userRepository.findById(user.getUserId()).get();
            listUserEntity.add(userEntity);
        }
        // map to output
        List<GroupChatMemberOutPut> groupMemberOutPutList = new ArrayList<>();
        for (UserEntity user : listUserEntity) {
            if (user.getId() == managerId) {
                groupMemberOutPutList.add(
                        GroupChatMemberOutPut.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .image(null)
                                .positionInGroup("Admin")
                                .build()
                );
            } else {
                groupMemberOutPutList.add(
                        GroupChatMemberOutPut.builder()
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

    public String addNewMember(GroupChatAddNewMemberInput groupChatAddNewMemberInput) {
        /*   get all userId from UserGroupChat, compare userId from Input,
            if it not exits then save new in UserGroupChat
            Can return a string("Add new successfully") if it success, or return
            string("The user is already in Group") if it doesn't*/
        List<UserGroupChatEntity> listUserGroupChatEntity = userGroupChatRepository
                .findAllByGroupId(
                        groupChatAddNewMemberInput.getGroupId()
                );
        List<Long> listUserIdExitInGroup = new ArrayList<>();
        for (UserGroupChatEntity userGroup : listUserGroupChatEntity) {
            listUserIdExitInGroup.add(userRepository.findById(
                    userGroup.getUserId()
            ).get().getId());
        }
        for (Long newUserId : groupChatAddNewMemberInput.getListUserId()) {
            if (!listUserIdExitInGroup.contains(newUserId)) {
                userGroupChatRepository.save(
                        UserGroupChatEntity.builder()
                                .groupId(groupChatAddNewMemberInput.getGroupId())
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

    public String deleteMember(String token, GroupChatDeleteMemberInput groupChatDeleteMemberInput) {
        Long checkUserId = TokenHelper.getUserIdFromToken(token);
        Long managerId = groupChatRepository
                .findById(groupChatDeleteMemberInput.getGroupId())
                .get().getManagerId();
        if (checkUserId != managerId) {
            return "You can't delete the others people";
        }

        Long userDeleteId = groupChatDeleteMemberInput.getUserId();
        if (userDeleteId == managerId) {
            return "You can't delete YourSelf";
        }
        userGroupChatRepository.deleteByUserIdAndGroupId(
                userDeleteId,
                groupChatDeleteMemberInput.getGroupId()
        );
        return "Success";

        // token.id = managerId => thi ms thuc hien
        // neu ma manageId == userId => k cho
    }

    @Transactional
    public void leaveTheGroupChat(GroupChatLeaveTheGroupInput groupChatLeaveTheGroupInput) {
         /* Need to check the position of managerGroup, if the manager leave,
        he will need to transfer his role to another
     */
        if (userGroupChatRepository.countByGroupId(
                groupChatLeaveTheGroupInput.getGroupId()) > 1) // check the number of row in table UserGroupChat
        {
            userGroupChatRepository.deleteByUserIdAndGroupId(
                    groupChatLeaveTheGroupInput.getUserId(),
                    groupChatLeaveTheGroupInput.getGroupId()
            );
        } else {
            // delte the last member in UserGroupChat table and delete immidiately table GroupChat with groupId
            userGroupChatRepository.deleteByUserIdAndGroupId(
                    groupChatLeaveTheGroupInput.getUserId(),
                    groupChatLeaveTheGroupInput.getGroupId()
            );
            groupChatRepository.deleteById(groupChatLeaveTheGroupInput.getGroupId());
        }

    }
}
