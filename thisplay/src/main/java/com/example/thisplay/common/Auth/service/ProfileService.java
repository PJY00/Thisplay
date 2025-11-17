package com.example.thisplay.common.Auth.service;

import com.example.thisplay.common.Auth.DTO.ProfileDTO;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.dto.CommentResponseDTO;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.entity.ReviewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    // 프로필 조회
    public ProfileDTO getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        return ProfileDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

    // 프로필 수정
    public ProfileDTO updateProfile(Long userId, ProfileDTO dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getProfileImgUrl() != null) user.setProfileImgUrl(dto.getProfileImgUrl());

        userRepository.save(user);
        return ProfileDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }
}