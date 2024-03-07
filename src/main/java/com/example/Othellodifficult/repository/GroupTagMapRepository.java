package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.GroupTagMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTagMapRepository extends JpaRepository<GroupTagMapEntity, Long> {
}
