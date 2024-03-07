package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final CommentMapRepository commentMapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TagRepository tagRepository;
    private final ChatRepository chatRepository;

    public ChatEntity getChat(Long chatId){
        return chatRepository.findById(chatId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

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

    public GroupEntity getGroup(Long groupId){
        return groupRepository.findById(groupId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

    public TagEntity getTag(Long tagId){
        return tagRepository.findById(tagId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }
}
