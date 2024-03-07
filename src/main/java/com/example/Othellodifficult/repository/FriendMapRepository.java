package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.friend.FriendMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendMapRepository extends JpaRepository<FriendMapEntity, Long> {
    @Query(value = "select u from FriendMapEntity u where u.userId1 = :userId or u.userId2 = :userId")
    Page<FriendMapEntity> findAllByUserId(Long userId, Pageable pageable);

    @Query(value = "select u from FriendMapEntity u where u.userId1 = :userId or u.userId2 = :userId")
    List<FriendMapEntity> findAllByUserId(Long userId);

    void deleteAllByUserId1AndUserId2(Long userId1, Long userId2);
}
