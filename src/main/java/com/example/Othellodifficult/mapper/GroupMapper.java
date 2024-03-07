package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.group.GroupOutput;
import com.example.Othellodifficult.entity.GroupEntity;
import org.mapstruct.Mapper;

@Mapper
public interface GroupMapper {
    GroupOutput getOutputFromEntity(GroupEntity groupEntity);
}
