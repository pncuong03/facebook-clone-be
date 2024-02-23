package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.UserGroupChatEntity;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserGroupChatRepository extends JpaRepository<UserGroupChatEntity, Long> {
    List<UserGroupChatEntity> findAllByGroupId(Long groupId);
    void deleteByUserIdAndGroupId(Long userId,Long groupId);
    Long countByGroupId(Long groupId);
//    @Query(
//            Select u. from UserEntity
//    )
//    List<Long> listUserId findAllUserIdBy(Long groupId);
}
