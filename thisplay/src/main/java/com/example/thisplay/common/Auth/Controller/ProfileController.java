package com.example.thisplay.common.Auth.Controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.DTO.ProfileDTO;
import com.example.thisplay.common.Auth.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 조회
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태가 아닙니다");
        }

        if (!id.equals(userDetails.getUserEntity().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("권한이 없습니다");
        }

        return ResponseEntity.ok(profileService.getProfile(id));
    }

    // 프로필 수정
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody ProfileDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태가 아닙니다");
        }

        if (!id.equals(userDetails.getUserEntity().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("권한이 없습니다");
        }

        return ResponseEntity.ok(profileService.updateProfile(id, dto));
    }
}
