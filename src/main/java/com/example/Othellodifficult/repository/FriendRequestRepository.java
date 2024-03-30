package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.friend.FriendRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
    void deleteByReceiverIdAndSenderId(Long receiverId, Long senderId);
    Boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<FriendRequestEntity> findAllBySenderId(Long senderId);
    Page<FriendRequestEntity> findAllByReceiverId(Long receiverId, Pageable pageable);
}
