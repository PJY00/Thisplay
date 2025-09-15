package com.example.thisplay.Controller;

import com.example.thisplay.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final UserRepository userRepository;

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody(required = false) Map<String, String> request,
                                      HttpServletRequest httpRequest,
                                      HttpServletResponse httpResponse) {
        // 1. refreshToken 추출 (우선순위: request body → cookie)
        String refreshToken = null;
        if (request != null && request.get("refreshToken") != null) {
            refreshToken = request.get("refreshToken");
        } else {
            // 쿠키에서 refreshToken 찾기
            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        Map<String, String> response = new HashMap<>();

        if (refreshToken != null) {
            // 2. DB에서 refreshToken 삭제
            Optional<com.example.thisplay.Entity.UserEntity> userOpt = userRepository.findByRefreshToken(refreshToken);
            userOpt.ifPresent(user -> {
                user.setRefreshToken(null);
                userRepository.save(user);
            });

            // 3. 쿠키 무효화
            Cookie accessCookie = new Cookie("accessToken", null);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(0);

            Cookie refreshCookie = new Cookie("refreshToken", null);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0);

            httpResponse.addCookie(accessCookie);
            httpResponse.addCookie(refreshCookie);

            response.put("message", "로그아웃 성공");
        } else {
            response.put("message", "로그아웃 실패: refreshToken 없음");
        }

        return response;
    }
}
