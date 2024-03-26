package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.post.CreatePostGroupInput;
import com.example.Othellodifficult.dto.post.CreatePostInput;
import com.example.Othellodifficult.dto.post.PostOutput;
import com.example.Othellodifficult.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PostMapper {
    PostEntity getEntityFromInput(CreatePostInput createPostInput);
    PostEntity getEntityFromInput(CreatePostGroupInput createPostGroupInput);
    void updateEntityFromInput(@MappingTarget PostEntity postEntity, CreatePostInput createPostInput);
    PostOutput getOutputFromEntity(PostEntity postEntity);
}
