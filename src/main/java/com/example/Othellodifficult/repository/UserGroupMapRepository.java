package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserGroupMapEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserGroupMapRepository extends JpaRepository<UserGroupMapEntity, Long> {
    Boolean existsByUserIdInAndGroupId(Collection<Long> userIds, Long groupId);
    Boolean existsByUserIdAndGroupId(Long userId, Long groupId);
    List<UserGroupMapEntity> findAllByGroupId(Long groupId);
    Page<UserGroupMapEntity> findAllByGroupId(Long groupId, Pageable pageable);
    void deleteByUserIdAndGroupId(Long userId,Long groupId);
    Long countByGroupId(Long groupId);
    UserGroupMapEntity findByGroupIdAndRole(Long groupId, String role);
}
