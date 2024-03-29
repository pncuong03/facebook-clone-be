package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.post.CreatePostGroupInput;
import com.example.Othellodifficult.dto.post.CreatePostInput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.service.PostGroupService;
import com.example.Othellodifficult.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/post-group")
@AllArgsConstructor
public class PostGroupController {
    private final PostGroupService postGroupService;

    @Operation(summary = "Danh sách bài viết trong group")
    @GetMapping("/get-post")
    public Page<PostOutput> getPostsOfFriends(@RequestHeader("Authorization") String accessToken,
                                              @RequestParam Long groupId,
                                              @ParameterObject Pageable pageable){
        return postGroupService.getPostGroup(accessToken,groupId, pageable);
    }

    @Operation(summary = "Đăng bài viết")
    @PostMapping(value = "/post", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public void creatPost(@RequestHeader("Authorization") String accessToken,
                          @RequestPart("post_information") @Valid String createPostInformation,
                          @RequestPart(name = "images",required = false) List<MultipartFile> multipartFiles) throws JsonProcessingException {
        CreatePostGroupInput createPostGroupInput ;
        ObjectMapper objectMapper = new ObjectMapper();
        createPostGroupInput = objectMapper.readValue(createPostInformation, CreatePostGroupInput.class);
        postGroupService.creatPost(accessToken, createPostGroupInput,multipartFiles);
    }

    @Operation(summary = "Sửa bài viết")
    @PutMapping("/update")
    public void updatePost(@RequestHeader("Authorization") String accessToken,
                           @RequestPart("post_information") @Valid String updatePostInformation,
                           @RequestParam Long postId,
                           @RequestPart(name = "images") List<MultipartFile> multipartFiles) throws JsonProcessingException {
        CreatePostGroupInput updatePostInput;
        ObjectMapper objectMapper = new ObjectMapper();
        updatePostInput = objectMapper.readValue(updatePostInformation,CreatePostGroupInput.class);
        postGroupService.updatePost(accessToken, postId, updatePostInput, multipartFiles);
    }

    @Operation(summary = "Xóa bài viết")
    @DeleteMapping("/delete")
    public void deletePost(@RequestHeader("Authorization") String accessToken,
                           @RequestParam Long postId,
                           @RequestParam Long groupId){
        postGroupService.deletePost(accessToken, postId,groupId);
    }
//
//    @Operation(summary = "Chia sẻ bài viết")
//    @PostMapping("/share")
//    public void sharePost(@RequestHeader("Authorization") String accessToken,
//                          @RequestParam Long shareId,
//                          @RequestBody @Valid CreatePostInput sharePostInput){
//        postService.sharePost(accessToken, shareId, sharePostInput);
//    }
//
//    @Operation(summary = "Danh sách bài viết (của mình)")
//    @GetMapping("/list/me")
//    public Page<PostOutput> getMyPost(@RequestHeader("Authorization") String accessToken,
//                                      @ParameterObject Pageable pageable){
//        return postService.getMyPosts(accessToken, pageable);
//    }
}
