package com.example.Othellodifficult.service;

import com.example.Othellodifficult.dto.TagOutput;
import com.example.Othellodifficult.entity.TagEntity;
import com.example.Othellodifficult.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    @Transactional(readOnly = true)
    public Page<TagOutput> getAllTag(){
        Pageable pageable = PageRequest.of(0,5);
        Page<TagEntity> tagEntities = tagRepository.findAll(pageable);
        return tagEntities.map(
                tagEntity -> {
                    return TagOutput.builder()
                            .id(tagEntity.getId())
                            .name(tagEntity.getName())
                            .build();
                }
        );
    }
}
