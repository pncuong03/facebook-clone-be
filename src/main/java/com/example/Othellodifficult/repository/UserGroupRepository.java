package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupEntity,Long> {
}
