package com.example.thisplay.common.Auth.repository;


import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.Entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByNickname(String nickname); //닉네임이 DB에 존재하는지
    Optional<UserEntity> findByNickname(String nickname); //닉네임으로 사용자 정보 조회
    Optional<UserEntity> findByRefreshToken(String refreshToken);//RefreshToken으로 사용자 정보를 조회.
    Optional<UserEntity> findByGoogleId(String googleId);


    List<UserEntity> findByStatus(UserStatus status);
}