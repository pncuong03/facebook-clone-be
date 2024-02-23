package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.FriendMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendMapRepository extends JpaRepository<FriendMapEntity,Long> {
}
