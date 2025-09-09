package com.example.thisplay.repository;


import com.example.thisplay.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByNickname(String nickname);
    UserEntity findByNickname(String nickname);
    Optional<UserEntity> findByRefreshToken(String refreshToken);
}