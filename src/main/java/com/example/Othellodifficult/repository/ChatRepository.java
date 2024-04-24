package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.entity.ChatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2);

    @Query("select u from ChatEntity u where u.userId1 =:userId1 and u.userId2 = :userId2")
    ChatEntity findByUserId(Long userId1, Long userId2);
    Page<ChatEntity> findAllByNameContainingIgnoreCaseAndIdIn(String name, Collection<Long> chatIds, Pageable pageable);
    Page<ChatEntity> findAllByIdIn(Collection<Long> chatIds, Pageable pageable);


    void deleteAllByUserId1AndUserId2(Long userId1, Long userId2);
}
