package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.chat.*;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserChatEntity;
import com.example.Othellodifficult.mapper.ChatMapper;
import com.example.Othellodifficult.repository.ChatRepository;
import com.example.Othellodifficult.repository.UserChatRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(ChatInput chatInput, String token) {
        Long managerId = TokenHelper.getUserIdFromToken(token);
        ChatEntity chatEntity = chatMapper.getEntityFromInput(chatInput);

        chatEntity.setManagerId(managerId);
        chatEntity.setNewestChatTime(LocalDateTime.now());
        chatEntity.setChatType(Common.GROUP);

        chatRepository.save(chatEntity);

        userChatRepository.save(
                UserChatEntity.builder()
                        .userId(managerId)
                        .groupId(chatEntity.getId())
                        .build()
        );

        for (Long userId : chatInput.getUserId()) {
            userChatRepository.save(
                    UserChatEntity.builder()
                            .userId(userId)
                            .groupId(chatEntity.getId())
                            .build()
            );
        }

    }

    public List<ChatMemberOutput> getGroupChatMember(Long groupId) {
        // get  managerId
        List<UserChatEntity> listUserChatEntity = userChatRepository.findAllByGroupId(groupId);
        ChatEntity chatEntity = chatRepository.findById(groupId).get();
        Long managerId = chatEntity.getManagerId();
        // get infor member
        List<UserEntity> listUserEntity = new ArrayList<>();
        for (UserChatEntity user : listUserChatEntity) {
            UserEntity userEntity = userRepository.findById(user.getUserId()).get();
            listUserEntity.add(userEntity);
        }
        // map to output
        List<ChatMemberOutput> groupMemberOutPutList = new ArrayList<>();
        for (UserEntity user : listUserEntity) {
            if (user.getId() == managerId) {
                groupMemberOutPutList.add(
                        ChatMemberOutput.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .image(null)
                                .positionInGroup("Admin")
                                .build()
                );
            } else {
                groupMemberOutPutList.add(
                        ChatMemberOutput.builder()
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

    public String addNewMember(ChatAddNewMemberInput chatAddNewMemberInput) {
        /*   get all userId from UserGroupChat, compare userId from Input,
            if it not exits then save new in UserGroupChat
            Can return a string("Add new successfully") if it success, or return
            string("The user is already in Group") if it doesn't*/
        List<UserChatEntity> listUserChatEntity = userChatRepository
                .findAllByGroupId(
                        chatAddNewMemberInput.getGroupId()
                );
        List<Long> listUserIdExitInGroup = new ArrayList<>();
        for (UserChatEntity userGroup : listUserChatEntity) {
            listUserIdExitInGroup.add(userRepository.findById(
                    userGroup.getUserId()
            ).get().getId());
        }
        for (Long newUserId : chatAddNewMemberInput.getListUserId()) {
            if (!listUserIdExitInGroup.contains(newUserId)) {
                userChatRepository.save(
                        UserChatEntity.builder()
                                .groupId(chatAddNewMemberInput.getGroupId())
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

    public String deleteMember(String token, ChatDeleteMemberInput chatDeleteMemberInput) {
        Long checkUserId = TokenHelper.getUserIdFromToken(token);
        Long managerId = chatRepository
                .findById(chatDeleteMemberInput.getGroupId())
                .get().getManagerId();
        if (checkUserId != managerId) {
            return "You can't delete the others people";
        }

        Long userDeleteId = chatDeleteMemberInput.getUserId();
        if (userDeleteId == managerId) {
            return "You can't delete YourSelf";
        }
        userChatRepository.deleteByUserIdAndGroupId(
                userDeleteId,
                chatDeleteMemberInput.getGroupId()
        );
        return "Success";

        // token.id = managerId => thi ms thuc hien
        // neu ma manageId == userId => k cho
    }

    @Transactional
    public void leaveTheGroupChat(ChatLeaveTheGroupInput chatLeaveTheGroupInput) {
         /* Need to check the position of managerGroup, if the manager leave,
        he will need to transfer his role to another
     */
        if (userChatRepository.countByGroupId(
                chatLeaveTheGroupInput.getGroupId()) > 1) // check the number of row in table UserGroupChat
        {
            userChatRepository.deleteByUserIdAndGroupId(
                    chatLeaveTheGroupInput.getUserId(),
                    chatLeaveTheGroupInput.getGroupId()
            );
        } else {
            // delte the last member in UserGroupChat table and delete immidiately table GroupChat with groupId
            userChatRepository.deleteByUserIdAndGroupId(
                    chatLeaveTheGroupInput.getUserId(),
                    chatLeaveTheGroupInput.getGroupId()
            );
            chatRepository.deleteById(chatLeaveTheGroupInput.getGroupId());
        }

    }
}
