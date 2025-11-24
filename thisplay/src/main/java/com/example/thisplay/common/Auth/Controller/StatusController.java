package com.example.thisplay.common.Auth.Controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.Entity.UserStatus;
import com.example.thisplay.common.Auth.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {
    //이거 STAR로 바꿔달라는 요청은 어떻게 처리하지...?
    private final UserStatusService userStatusService;

    @PatchMapping("/user/{nickname}/star")
    public ResponseEntity<String> changeUserStatus(
            @PathVariable String nickname,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        if (userDetails == null) {
            throw new RuntimeException("인증 정보가 없습니다. 토큰이 유효한지 확인하세요.");
        }

        UserEntity currentUser = userDetails.getUserEntity();
        userStatusService.changeUserStatus(nickname, UserStatus.STAR, currentUser);
        return ResponseEntity.ok(nickname+"의 상태가 STAR로 변경되었습니다.");
    }
}
