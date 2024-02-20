package com.example.Othellodifficult.mapper;

import com.example.Othellodifficult.dto.difficult.DifficultRequest;
import com.example.Othellodifficult.dto.difficult.DifficultResponse;
import com.example.Othellodifficult.entity.DifficultEntity;

public class DifficultMapper {
    public static DifficultEntity getEntityFromInput(DifficultRequest request){
        return new DifficultEntity(null, request.getAmount(), null);
    }

    public static DifficultResponse getResponseFromEntity(DifficultEntity entity){
        return new DifficultResponse(entity.getId(), entity.getAmount());
    }
}
