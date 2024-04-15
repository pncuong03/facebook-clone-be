package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    List<TagEntity> findAllByIdIn(Collection<Long> tagIds);
}
