package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.DifficultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DifficultRepository extends JpaRepository<DifficultEntity, Long> {
    List<DifficultEntity> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
