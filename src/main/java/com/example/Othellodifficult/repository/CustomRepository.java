package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.entity.CommentMapEntity;
import com.example.Othellodifficult.entity.PostEntity;
import com.example.Othellodifficult.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final CommentMapRepository commentMapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentMapEntity getCommentMap(Long commentMapId){
        return commentMapRepository.findById(commentMapId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

    public PostEntity getPost(Long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

    public UserEntity getUser(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }
}
