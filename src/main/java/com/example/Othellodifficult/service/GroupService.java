package com.example.Othellodifficult.service;

import com.example.Othellodifficult.dto.group.GroupInput;
import com.example.Othellodifficult.entity.GroupEntity;
import com.example.Othellodifficult.entity.UserGroupEntity;
import com.example.Othellodifficult.repository.GroupRepository;
import com.example.Othellodifficult.repository.UserGroupRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
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
}
