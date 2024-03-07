package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2);
}
