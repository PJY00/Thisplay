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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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
    public ProfileDTO updateProfile(Long userId, String nickname, MultipartFile profileImage) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        // 닉네임 변경
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
        }

        // 프로필 이미지 변경
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String imageUrl = fileStorageService.saveProfileImage(userId, profileImage);
                user.setProfileImgUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 중 오류 발생", e);
            }
        }

        userRepository.save(user);

        return ProfileDTO.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }
}