package com.example.thisplay.common.setting.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.setting.dto.FontRequestDto;
import com.example.thisplay.common.setting.service.FontService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestBody FontRequestDto requestDto
    ) {
        // SecurityContext에서 직접 principal 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("로그인된 사용자만 이용할 수 있습니다.");
        }

        // WTFilter & LoginFilter가 넣은 CustomUserDetails 변환
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        //DB UserEntity를 꺼낼 수 있음
        UserEntity user = userDetails.getUserEntity();

        System.out.println("현재 로그인한 사용자: " + user.getUserId());

        String message = fontService.updateFontSize(user, requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}
