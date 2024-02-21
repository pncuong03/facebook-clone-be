package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupChatRepository extends JpaRepository<UserGroupChatEntity,Long> {
    List<UserGroupChatEntity> findAllByGroupId(Long groupId);
}
