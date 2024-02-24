package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserChatRepository extends JpaRepository<UserChatEntity, Long> {
    List<UserChatEntity> findAllByGroupId(Long groupId);
    void deleteByUserIdAndGroupId(Long userId,Long groupId);
    Long countByGroupId(Long groupId);
//    @Query(
//            Select u. from UserEntity
//    )
//    List<Long> listUserId findAllUserIdBy(Long groupId);
}
