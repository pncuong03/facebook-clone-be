package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long> {
    Boolean existsByUserIdInAndGroupId(Collection<Long> userIds, Long groupId);
    Boolean existsByUserIdAndGroupId(Long userId, Long groupId);
    List<UserGroupEntity> findAllByGroupId(Long groupId);
    Page<UserGroupEntity> findAllByGroupId(Long groupId, Pageable pageable);
    void deleteByUserIdAndGroupId(Long userId,Long groupId);
    Long countByGroupId(Long groupId);
}
