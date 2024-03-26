package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByUserId(Long userId, Pageable pageable);
    Page<PostEntity> findAllByGroupId(Long groupId, Pageable pageable);
    Page<PostEntity> findAllByUserIdAndState(Long userId, String state, Pageable pageable);
    List<PostEntity> findAllByIdIn(List<Long> postIds);
    Page<PostEntity> findAllByUserIdInAndState(Collection<Long> userIds, String state, Pageable pageable);
}
