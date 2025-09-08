package com.example.thisplay.service;

import com.example.thisplay.DTO.JoinDTO;
import com.example.thisplay.Entity.UserEntity;
import com.example.thisplay.repository.UserRepository;
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

        // 유저 생성
        UserEntity user = UserEntity.builder()
                .nickname(nickname)
                .password(bCryptPasswordEncoder.encode(password))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

        return "회원가입 성공";
    }
}
