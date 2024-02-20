package com.example.Othellodifficult.repository;

import com.example.Othellodifficult.entity.DifficultEntity;
import com.example.Othellodifficult.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
    Boolean existsByUsername(String username);
}
