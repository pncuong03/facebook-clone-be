package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.chat.*;
import com.example.Othellodifficult.entity.ChatEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserChatMapEntity;
import com.example.Othellodifficult.mapper.ChatMapper;
import com.example.Othellodifficult.repository.ChatRepository;
import com.example.Othellodifficult.repository.CustomRepository;
import com.example.Othellodifficult.repository.UserChatRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupChatService {
    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatMapRepository;
    private final UserRepository userRepository;
    private final CustomRepository customRepository;

    private ChatEntity detail(Long chatId) { // Class
        return chatRepository.findById(chatId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

    @Transactional
    public void createGroupChat(CreateGroupChatInput createGroupChatInput, String accessToken) {
        Long managerId = TokenHelper.getUserIdFromToken(accessToken);
        if (createGroupChatInput.getUserIds().contains(managerId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        ChatEntity chatEntity = chatMapper.getEntityFromInput(createGroupChatInput);

        chatEntity.setManagerId(managerId);
        chatEntity.setNewestChatTime(LocalDateTime.now());
        chatEntity.setChatType(Common.GROUP);
        chatEntity.setNewestUserId(managerId);
        chatEntity.setNewestMessage("Created Group");


        chatRepository.save(chatEntity);

        userChatMapRepository.save(
                UserChatMapEntity.builder()
                        .userId(managerId)
                        .chatId(chatEntity.getId())
                        .build()
        );

        for (Long userId : createGroupChatInput.getUserIds()) {
            userChatMapRepository.save(
                    UserChatMapEntity.builder()
                            .userId(userId)
                            .chatId(chatEntity.getId())
                            .build()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ChatMemberOutput> getGroupChatMembersBy(Long groupChatId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (Boolean.FALSE.equals(userChatMapRepository.existsByUserIdAndChatId(userId, groupChatId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<UserChatMapEntity> userChatEntities = userChatMapRepository.findAllByChatId(groupChatId);
        ChatEntity chatEntity = detail(groupChatId);
        Long managerId = chatEntity.getManagerId();

        List<UserEntity> userEntities = userRepository.findAllByIdIn(
                userChatEntities.stream().map(UserChatMapEntity::getId).collect(Collectors.toList())
        );

        List<ChatMemberOutput> groupMemberOutputs = new ArrayList<>();
        for (UserEntity user : userEntities) {
            if (Objects.equals(user.getId(), managerId)) {
                groupMemberOutputs.add(
                        ChatMemberOutput.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .imageUrl(user.getImageUrl())
                                .role(Common.ADMIN)
                                .build()
                );
            } else {
                groupMemberOutputs.add(
                        ChatMemberOutput.builder()
                                .id(user.getId())
                                .fullName(user.getFullName())
                                .imageUrl(user.getImageUrl())
                                .role(Common.MEMBER)
                                .build()
                );
            }
        }
        return groupMemberOutputs;
    }

    @Transactional
    public void addNewMemberToGroupChat(ChatAddNewMemberInput chatAddNewMemberInput, String accessToken) {
        ChatEntity chatEntity = customRepository.getChat(chatAddNewMemberInput.getGroupChatId());
        if (!Objects.equals(chatEntity.getManagerId(), TokenHelper.getUserIdFromToken(accessToken))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<UserChatMapEntity> userChatMapEntities =
                userChatMapRepository.findAllByChatId(chatAddNewMemberInput.getGroupChatId());

        List<Long> userIdsInGroup = userChatMapEntities.stream()
                .map(UserChatMapEntity::getUserId)
                .collect(Collectors.toList());

        for (Long newUserId : chatAddNewMemberInput.getUserIds()) {
            if (!userIdsInGroup.contains(newUserId)) {
                userChatMapRepository.save(
                        UserChatMapEntity.builder()
                                .chatId(chatAddNewMemberInput.getGroupChatId())
                                .userId(newUserId)
                                .build()
                );
            } else {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }
    }

    @Transactional
    public void deleteMember(String accessToken, ChatDeleteMemberInput chatDeleteMemberInput) {
        ChatEntity chatEntity = chatRepository.findById(chatDeleteMemberInput.getGroupChatId()).get();
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (!Objects.equals(chatEntity.getManagerId(), userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if (Objects.equals(chatDeleteMemberInput.getUserId(), userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        userChatMapRepository.deleteByUserIdAndChatId(
                chatDeleteMemberInput.getUserId(),
                chatDeleteMemberInput.getGroupChatId()
        );
    }

    @Transactional
    public void leaveTheGroupChat(ChatLeaveTheGroupInput chatLeaveTheGroupInput) {
        if (userChatMapRepository.countByChatId(chatLeaveTheGroupInput.getGroupId()) > 1) {
            userChatMapRepository.deleteByUserIdAndChatId(
                    chatLeaveTheGroupInput.getUserId(),
                    chatLeaveTheGroupInput.getGroupId()
            );
        } else {
            userChatMapRepository.deleteByUserIdAndChatId(
                    chatLeaveTheGroupInput.getUserId(),
                    chatLeaveTheGroupInput.getGroupId()
            );
            chatRepository.deleteById(chatLeaveTheGroupInput.getGroupId());
        }
    }
}
