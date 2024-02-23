package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserGroupChatEntity;
import com.example.Othellodifficult.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long> {
    List<UserGroupEntity> findAllByGroupId(Long groupId);
    void deleteByUserIdAndGroupId(Long userId,Long groupId);
    Long countByGroupId(Long groupId);
}
