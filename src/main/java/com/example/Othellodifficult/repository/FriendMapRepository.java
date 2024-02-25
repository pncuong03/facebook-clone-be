package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.FriendMapEntity;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendMapRepository extends JpaRepository<FriendMapEntity, Long> {
    @Query(value = "select u from FriendMapEntity u where u.userId_1 = :userId or u.userId_2 = :userId")
    List<FriendMapEntity> findAllByUserId(Long userId);
}
