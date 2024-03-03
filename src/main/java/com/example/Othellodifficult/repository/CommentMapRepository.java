package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.CommentMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapRepository extends JpaRepository<CommentMapEntity, Long> {
    List<CommentMapEntity> findAllByPostIdAndAndCommentId(Long postId, Long commentId);
}
