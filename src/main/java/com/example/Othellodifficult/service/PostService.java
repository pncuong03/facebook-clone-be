package com.example.Othellodifficult.service;

import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.post.CreatePostInput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.entity.LikeMapEntity;
import com.example.Othellodifficult.entity.PostEntity;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.entity.friend.FriendMapEntity;
import com.example.Othellodifficult.helper.StringUtils;
import com.example.Othellodifficult.mapper.PostMapper;
import com.example.Othellodifficult.repository.*;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeMapRepository likeMapRepository;
    private final FriendMapRepository friendMapRepository;
    private final CustomRepository customRepository;

    @Transactional(readOnly = true)
    public Page<PostOutput> getPostsOfFriends(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<FriendMapEntity> friendMapEntities = friendMapRepository.findAllByUserId(userId);
        Set<Long> friendIds = new HashSet<>();
        for (FriendMapEntity friendMapEntity : friendMapEntities) {
            friendIds.add(friendMapEntity.getUserId1());
            friendIds.add(friendMapEntity.getUserId2());
        }
        friendIds = friendIds.stream().filter(id -> !id.equals(userId)).collect(Collectors.toSet());

        Page<PostEntity> postEntitiesOfFriends = postRepository.findAllByUserIdInAndState(friendIds, Common.PUBLIC, pageable);
        if (Objects.isNull(postEntitiesOfFriends) || postEntitiesOfFriends.isEmpty()) {
            return Page.empty();
        }

        Map<Long, UserEntity> friendMapEntityMap = userRepository.findAllByIdIn(
                        postEntitiesOfFriends.stream().map(PostEntity::getUserId).distinct().collect(Collectors.toList())
                ).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return mapResponsePostPage(postEntitiesOfFriends, friendMapEntityMap);
    }

    @Transactional(readOnly = true)
    public Page<PostOutput> getMyPost(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<PostEntity> postEntityPage = postRepository.findAllByUserId(userId, pageable);
        if (Objects.isNull(postEntityPage) || postEntityPage.isEmpty()) {
            return Page.empty();
        }
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
        Map<Long, UserEntity> userEntityMap = new HashMap<>();
        userEntityMap.put(userEntity.getId(), userEntity);
        return mapResponsePostPage(postEntityPage, userEntityMap);
    }

    @Transactional
    public void creatPost(String accessToken, CreatePostInput createPostInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        PostEntity postEntity = postMapper.getEntityFromInput(createPostInput);
        postEntity.setImageUrlsString(StringUtils.convertListToString(createPostInput.getImageUrls()));
        postEntity.setUserId(userId);
        postEntity.setLikeCount(0);
        postEntity.setCommentCount(0);
        postEntity.setShareCount(0);
        postEntity.setCreatedAt(LocalDateTime.now());
        postRepository.save(postEntity);
    }

    @Transactional
    public void updatePost(String accessToken, Long postId, CreatePostInput updatePostInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        PostEntity postEntity = customRepository.getPost(postId);
        if (!userId.equals(postEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        postMapper.updateEntityFromInput(postEntity, updatePostInput);
        postEntity.setImageUrlsString(StringUtils.convertListToString(updatePostInput.getImageUrls()));
        postRepository.save(postEntity);
    }

    @Transactional
    public void deletePost(String accessToken, Long postId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        PostEntity postEntity = customRepository.getPost(postId);
        if (!userId.equals(postEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        postRepository.delete(postEntity);
    }

    @Transactional
    public void sharePost(String accessToken, Long shareId, CreatePostInput sharePostInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        PostEntity postEntity = customRepository.getPost(shareId);
        if (Objects.nonNull(postEntity.getShareId())) {
            shareId = postEntity.getShareId();
        }
        Long finalShareId = shareId;
        CompletableFuture.runAsync(() -> {
            likeMapRepository.save(
                    LikeMapEntity.builder()
                            .postId(finalShareId)
                            .userId(userId)
                            .build()
            );
            PostEntity finalShareEntity = customRepository.getPost(finalShareId);;
            Integer shareCount = finalShareEntity.getShareCount();
            finalShareEntity.setLikeCount(++shareCount);
            postRepository.save(finalShareEntity);
        });

        if (userId.equals(postEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        PostEntity sharePostEntity = postMapper.getEntityFromInput(sharePostInput);
        sharePostEntity.setImageUrlsString(null);
        sharePostEntity.setShareId(shareId);
        sharePostEntity.setLikeCount(0);
        sharePostEntity.setCommentCount(0);
        sharePostEntity.setShareCount(0);
        sharePostEntity.setCreatedAt(LocalDateTime.now());
        postRepository.save(sharePostEntity);
    }

    private Page<PostOutput> mapResponsePostPage(Page<PostEntity> postEntityPage, Map<Long, UserEntity> userEntityMap) {
        List<Long> shareIds = new ArrayList<>();
        for (PostEntity postEntity : postEntityPage) {
            if (Objects.nonNull(postEntity.getShareId())) {
                shareIds.add(postEntity.getShareId());
            }
        }

        Map<Long, PostOutput> sharePostOutputMap;
        if (!shareIds.isEmpty()) {
            List<PostEntity> sharePostEntities = postRepository.findAllByIdIn(shareIds);

            List<PostOutput> sharePostOutputs = sharePostEntities.stream() // entity
                    .map(postEntity -> {
                        PostOutput postOutput = postMapper.getOutputFromEntity(postEntity);
                        postOutput.setImageUrls(StringUtils.getListFromString(postEntity.getImageUrlsString()));
                        return postOutput;
                    }) // output
                    .collect(Collectors.toList());

            sharePostOutputMap = sharePostOutputs.stream().collect(Collectors.toMap(PostOutput::getId, Function.identity()));

            List<Long> shareUserIds = sharePostOutputs.stream()
                    .map(PostOutput::getUserId)
                    .collect(Collectors.toList());

            Map<Long, UserEntity> shareUserEntiyMap = userRepository.findAllByIdIn(shareUserIds).stream()
                    .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

            sharePostOutputs.stream().map(
                    postOutput -> {
                        UserEntity user = shareUserEntiyMap.get(postOutput.getUserId());
                        postOutput.setImageUrl(user.getImageUrl());
                        postOutput.setFullName(user.getFullName());
                        return postOutput;
                    }
            ).collect(Collectors.toList());
        } else {
            sharePostOutputMap = new HashMap<>();
        }

        return postEntityPage.map(
                postEntity -> {
                    PostOutput postOutput = postMapper.getOutputFromEntity(postEntity);
                    postOutput.setFullName(userEntityMap.get(postEntity.getUserId()).getFullName());
                    postOutput.setImageUrl(userEntityMap.get(postEntity.getUserId()).getImageUrl());
                    postOutput.setImageUrls(StringUtils.getListFromString(postEntity.getImageUrlsString()));
                    if (Objects.nonNull(postOutput.getShareId())) {
                        PostOutput sharePostOutput = sharePostOutputMap.get(postOutput.getShareId());
                        if (sharePostOutput.getState().equals(Common.PRIVATE)) {
                            sharePostOutput = null;
                        }
                        postOutput.setSharePost(sharePostOutput);
                    }
                    return postOutput;
                }
        );
    }
}
