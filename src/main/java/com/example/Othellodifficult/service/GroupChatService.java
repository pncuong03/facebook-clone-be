package com.example.Othellodifficult.service;

import com.example.Othellodifficult.dto.groupchat.GroupChatInput;
import com.example.Othellodifficult.dto.groupchat.GroupMemberOutPut;
import com.example.Othellodifficult.entity.GroupChatEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupChatEntity;
import com.example.Othellodifficult.mapper.GroupChatMapper;
import com.example.Othellodifficult.repository.GroupChatRepository;
import com.example.Othellodifficult.repository.UserGroupChatRepository;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
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
                        .build());

        for (Long userId : groupChatInput.getUserId()) {
            userGroupChatRepository.save(
                    UserGroupChatEntity.builder()
                            .userId(userId)
                            .groupId(groupChatEntity.getId())
                            .build()
            );
        }

    }

    public List<GroupMemberOutPut> getGroupMember(Long groupId){
        // lấy dc id member từ groupId
        List<UserGroupChatEntity> listUserGroupChatEntity = userGroupChatRepository.findAllByGroupId(groupId);
        Long managerId = groupChatRepository.findById(groupId).get().getManagerId();
        // lấy thôn tin member
        List<UserEntity> listUserEntity = new ArrayList<>();
        for(UserGroupChatEntity user : listUserGroupChatEntity){
            UserEntity userEntity = userRepository.findById(user.getUserId()).get();
            listUserEntity.add(userEntity);
        }
        List<GroupMemberOutPut> groupMemberOutPutList = new ArrayList<>();
        for(UserEntity user:listUserEntity){
            if(user.getId() == managerId){
                groupMemberOutPutList.add(
                        GroupMemberOutPut.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .image(null)
                                .positionInGroup("Admin")
                                .build()
                );
            }else{
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
}
