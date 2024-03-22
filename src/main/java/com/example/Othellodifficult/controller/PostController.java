package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.post.CreatePostInput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/post")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "Danh sách bài viết PUBLIC của bạn bè")
    @PostMapping("/list/friends")
    public Page<PostOutput> getPostsOfFriends(@RequestHeader("Authorization") String accessToken,
                                              @ParameterObject Pageable pageable){
        return postService.getPostsOfFriends(accessToken, pageable);
    }

    @Operation(summary = "Đăng bài viết")
    @PostMapping("/post")
    public void creatPost(@RequestHeader("Authorization") String accessToken,
                          @RequestBody @Valid CreatePostInput createPostInput,
                          @RequestParam(name = "images") List<MultipartFile> multipartFiles){
        postService.creatPost(accessToken, createPostInput, multipartFiles);
    }

    @Operation(summary = "Sửa bài viết")
    @PutMapping("/update")
    public void updatePost(@RequestHeader("Authorization") String accessToken,
                           @RequestParam Long postId,
                           @RequestBody @Valid CreatePostInput updatePostInput,
                           @RequestParam(name = "images") List<MultipartFile> multipartFiles){
        postService.updatePost(accessToken, postId, updatePostInput, multipartFiles);
    }

    @Operation(summary = "Xóa bài viết")
    @DeleteMapping("/delete")
    public void deletePost(@RequestHeader("Authorization") String accessToken, @RequestParam Long postId){
        postService.deletePost(accessToken, postId);
    }

    @Operation(summary = "Chia sẻ bài viết")
    @PostMapping("/share")
    public void sharePost(@RequestHeader("Authorization") String accessToken,
                          @RequestParam Long shareId,
                          @RequestBody @Valid CreatePostInput sharePostInput){
        postService.sharePost(accessToken, shareId, sharePostInput);
    }

    @Operation(summary = "Danh sách bài viết (của mình)")
    @GetMapping("/list/me")
    public Page<PostOutput> getMyPost(@RequestHeader("Authorization") String accessToken,
                                      @ParameterObject Pageable pageable){
        return postService.getMyPosts(accessToken, pageable);
    }
}
