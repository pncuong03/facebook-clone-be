package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.LikeMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeMapRepository extends JpaRepository<LikeMapEntity, Long> {
    Boolean existsByUserIdAndPostId(Long userId, Long postId);
    void deleteAllByUserIdAndPostId(Long userId, Long postId);
    Page<LikeMapEntity> findAllByPostId(Long postId, Pageable pageable);
}
