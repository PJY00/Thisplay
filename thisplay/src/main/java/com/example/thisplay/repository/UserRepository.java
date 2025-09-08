package com.example.thisplay.repository;


import com.example.thisplay.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByNickname(String nickname);
    UserEntity findByNickname(String nickname);
}