package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.GroupTagMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface GroupTagMapRepository extends JpaRepository<GroupTagMapEntity, Long> {
    List<GroupTagMapEntity> findAllByGroupIdIn(Collection<Long> groupIds);

    List<GroupTagMapEntity> findAllByGroupId( Long groupId);
}
