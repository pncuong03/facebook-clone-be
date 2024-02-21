package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.GroupChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChatEntity, Long> {
}
