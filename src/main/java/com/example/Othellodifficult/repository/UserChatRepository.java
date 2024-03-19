package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserChatMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserChatRepository extends JpaRepository<UserChatMapEntity, Long> {
    List<UserChatMapEntity> findAllByChatId(Long groupId);
    List<UserChatMapEntity> findAllByUserId(Long userId);
    void deleteByUserIdAndChatId(Long userId,Long groupId);
    Long countByChatId(Long groupId);
    Boolean existsByUserIdAndChatId(Long userId, Long chatId);
}
