package com.example.thisplay.common.Auth.service;

import com.example.thisplay.common.Auth.DTO.JoinDTO;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.global.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String join(JoinDTO joinDTO) {
        String nickname = joinDTO.getNickname();
        String password = joinDTO.getPassword();

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(nickname)) {
            return "이미 존재하는 닉네임입니다.";
        }

        // 비밀번호 유효성 검사
        if (!PasswordValidator.isValid(password)) {
            return "비밀번호는 8~20자, 영문 대/소문자·숫자·특수문자를 모두 포함해야 합니다.";
        }

        // 유저 생성
        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .profileImgUrl("/images/profile/default.png")
                .build();

        userRepository.save(user);

        return "회원가입 성공";
    }
}
