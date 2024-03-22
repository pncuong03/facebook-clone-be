package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.post.CommentOutput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.dto.user.UserOutput;
import com.example.Othellodifficult.entity.*;
import com.example.Othellodifficult.entity.message.EventNotificationEntity;
import com.example.Othellodifficult.helper.StringUtils;
import com.example.Othellodifficult.mapper.PostMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.EventHelper;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInteractService {
    private final PostRepository postRepository;
    private final LikeMapRepository likeMapRepository;
    private final CommentMapRepository commentMapRepository;
    private final CustomRepository customRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final NotificationRepository notificationRepository;

    // tbl_post

    @Transactional
    public void like(Long postId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (Boolean.TRUE.equals(likeMapRepository.existsByUserIdAndPostId(userId, postId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        PostEntity postEntity = postRepository.findById(postId).get();
        CompletableFuture.runAsync(() -> {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .type(Common.USER)
                            .userId(postEntity.getUserId())
                            .interactId(userId)
                            .interactType(Common.LIKE)
                            .postId(postId)
                            .hasSeen(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            likeMapRepository.save(
                    LikeMapEntity.builder()
                            .userId(userId)
                            .postId(postId)
                            .build()
            );
            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .eventType(Common.LIKE)
                            .userId(postEntity.getUserId())
                            .state(Common.NEW_EVENT)
                            .build()
            );
            EventHelper.pushEventForUserByUserId(postEntity.getUserId());
        });
        Integer likeCount = postEntity.getLikeCount();
        postEntity.setLikeCount(++likeCount);
        postRepository.save(postEntity);
    }

    @Transactional
    public void removeLike(Long postId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CompletableFuture.runAsync(() -> {
            likeMapRepository.deleteAllByUserIdAndPostId(userId, postId);
        });
        PostEntity postEntity = postRepository.findById(postId).get();
        Integer likeCount = postEntity.getLikeCount();
        postEntity.setLikeCount(--likeCount);
        postRepository.save(postEntity);
    }

    @Transactional
    public void comment(Long postId, String comment, Long commentId, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        PostEntity postEntity = postRepository.findById(postId).get();

        CompletableFuture.runAsync(() -> {
            notificationRepository.save(
                    NotificationEntity.builder()
                            .type(Common.USER)
                            .userId(postEntity.getUserId())
                            .interactId(userId)
                            .interactType(Common.COMMENT)
                            .postId(postId)
                            .hasSeen(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            commentMapRepository.save(
                    CommentMapEntity.builder()
                            .userId(userId)
                            .postId(postId)
                            .comment(comment.trim())
                            .commentId(commentId)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .eventType(Common.COMMENT)
                            .userId(postEntity.getUserId())
                            .state(Common.NEW_EVENT)
                            .build()
            );
            EventHelper.pushEventForUserByUserId(postEntity.getUserId());
        });

        Integer commentCount = postEntity.getCommentCount();
        postEntity.setLikeCount(++commentCount);
        postRepository.save(postEntity);
    }

    @Transactional
    public void removeComment(Long commentMapId, String accessToken) {
        CommentMapEntity commentMapEntity = customRepository.getCommentMap(commentMapId);
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (!userId.equals(commentMapEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        commentMapRepository.delete(commentMapEntity);
    }

    @Transactional(readOnly = true)
    public Page<UserOutput> getUsersLikeOfPost(Long postId, Pageable pageable) {
        PostEntity postEntity = customRepository.getPost(postId);
        if (postEntity.getState().equals(Common.PRIVATE)) {
            throw new RuntimeException(Common.UN_AUTHORIZATION);
        }
        Page<LikeMapEntity> likeMapEntityPage = likeMapRepository.findAllByPostId(postId, pageable);
        if (Objects.isNull(likeMapEntityPage) || likeMapEntityPage.isEmpty()) {
            return Page.empty();
        }
        Map<Long, UserEntity> userMap = userRepository.findAllByIdIn(
                likeMapEntityPage.stream().map(LikeMapEntity::getUserId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return likeMapEntityPage.map(
                likeMapEntity -> {
                    UserEntity user = userMap.get(likeMapEntity.getUserId());
                    return UserOutput.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .imageUrl(user.getImageUrl())
                            .build();
                }
        );
    }

    @Transactional(readOnly = true)
    public PostOutput getPostAndComment(Long postId, String accessToken) {
        PostEntity postEntity = customRepository.getPost(postId);
        UserEntity userEntity = customRepository.getUser(postEntity.getUserId());
        PostOutput postOutput = postMapper.getOutputFromEntity(postEntity);
        postOutput.setFullName(userEntity.getFullName());
        postOutput.setImageUrl(userEntity.getImageUrl());
        postOutput.setImageUrls(StringUtils.getListFromString(postEntity.getImageUrlsString()));
        if (Objects.nonNull(postEntity.getShareId())) {
            PostEntity sharedPostEntity = customRepository.getPost(postEntity.getShareId());
            UserEntity sharedUserEntity = customRepository.getUser(postEntity.getShareId());
            PostOutput sharedPostOutput = postMapper.getOutputFromEntity(sharedPostEntity);
            sharedPostOutput.setFullName(sharedUserEntity.getFullName());
            sharedPostOutput.setImageUrl(sharedUserEntity.getImageUrl());
            sharedPostOutput.setImageUrls(StringUtils.getListFromString(sharedPostEntity.getImageUrlsString()));
            postOutput.setSharePost(sharedPostOutput);
        }
        List<CommentMapEntity> commentMapEntities = commentMapRepository.findAllByPostIdAndAndCommentId(postId, null);
        if (Objects.isNull(commentMapEntities) || commentMapEntities.isEmpty()) {
            return postOutput;
        }

        Map<Long, UserEntity> userCommentMap = userRepository.findAllByIdIn(
                commentMapEntities.stream().map(CommentMapEntity::getUserId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<CommentOutput> commentOutputs = new ArrayList<>();
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        for (CommentMapEntity commentMapEntity : commentMapEntities) {
            UserEntity commentUser = userCommentMap.get(commentMapEntity.getUserId());
            commentOutputs.add(
                    CommentOutput.builder()
                            .id(commentMapEntity.getId())
                            .postId(commentMapEntity.getPostId())
                            .userId(commentMapEntity.getUserId())
                            .comment(commentMapEntity.getComment())
                            .createdAt(commentMapEntity.getCreatedAt())
                            .fullName(commentUser.getFullName())
                            .imageUrl(commentUser.getImageUrl())
                            .canDelete(userId.equals(commentUser.getId()))
                            .build()
            );
        }
        postOutput.setComments(commentOutputs);
        return postOutput;
    }
}
