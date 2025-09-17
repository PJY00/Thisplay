package com.example.thisplay.common.Auth.Controller;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.global.jwt.JWTUtil;
import com.example.thisplay.common.Auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @GetMapping("/loginSuccess")
    public Map<String, Object> loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // DB에서 유저 찾기 or 새로 생성
        Optional<UserEntity> optionalUser = userRepository.findByNickname(email);
        UserEntity user = optionalUser.orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setNickname(email);  // 구글 이메일을 닉네임처럼 저장
            newUser.setName(name);
            newUser.setRole("ROLE_USER"); // 기본 권한
            return userRepository.save(newUser);
        });

        // JWT 발급
        String accessToken = jwtUtil.createJwt(user.getNickname(), user.getRole(), 10 * 60 * 1000L);  // 10분
        String refreshToken = jwtUtil.createJwt(user.getNickname(), user.getRole(), 24 * 60 * 60 * 1000L); // 24시간

        // DB에 RefreshToken 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 응답 JSON
        Map<String, Object> response = new HashMap<>();
        response.put("message", "구글 로그인 성공");
        response.put("name", name);
        response.put("email", email);
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return response;
    }

    @GetMapping("/loginFailure")
    public Map<String, Object> loginFailure() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "구글 로그인 실패");
        return response;
    }
}
