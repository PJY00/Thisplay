package com.example.thisplay.Controller;

import com.example.thisplay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final UserRepository userRepository;

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        Map<String, String> response = new HashMap<>();

        // refreshToken이 DB에 존재하면 삭제
        userRepository.findByRefreshToken(refreshToken).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });

        response.put("message", "로그아웃 성공");
        return response;
    }
}
