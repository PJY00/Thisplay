package com.example.thisplay.common.setting.repository;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.setting.entity.FontEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FontRepository extends JpaRepository<FontEntity, Long> {
    Optional<FontEntity> findByUser(UserEntity user);
}
