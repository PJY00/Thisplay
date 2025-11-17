package com.example.thisplay.common.setting.controller;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.setting.dto.FontRequestDto;
import com.example.thisplay.common.setting.service.FontService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting")
public class FontController {

    private final FontService fontService;

    @PutMapping("/font-size")
    public ResponseEntity<Map<String, Object>> updateFontSize(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody FontRequestDto requestDto
    ) {
        System.out.println("현재 로그인한 사용자: " + (user == null ? "NULL" : user.getUserId()));

        String message = fontService.updateFontSize(user, requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

}
