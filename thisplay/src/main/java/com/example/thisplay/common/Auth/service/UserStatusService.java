package com.example.thisplay.common.Auth.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.Entity.UserStatus;
import com.example.thisplay.common.Auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStatusService {
    private final UserRepository userRepository;

    @Transactional
    public void changeUserStatus(String targetNickname, UserStatus newStatus, UserEntity currentUser) {

        // ✅ 관리자 권한 확인
        if (!currentUser.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("관리자만 변경할 수 있습니다.");
        }

        // ✅ 상태 변경할 대상 유저 조회
        UserEntity targetUser = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new RuntimeException("해당 닉네임의 유저가 없습니다."));

        // ✅ 상태 변경
        targetUser.setStatus(newStatus);
    }
}
