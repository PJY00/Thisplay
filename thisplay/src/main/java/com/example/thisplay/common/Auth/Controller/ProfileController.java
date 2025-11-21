package com.example.thisplay.common.Auth.Controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.DTO.ProfileDTO;
import com.example.thisplay.common.Auth.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 조회
    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileDTO> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    // 내 프로필 조회
    @GetMapping("/me/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        Long id = userDetails.getUserEntity().getUserId();
        ProfileDTO profile = profileService.getProfile(id);
        return ResponseEntity.ok(profile);
    }

    // 프로필 수정
    @PatchMapping(
            value = "/{id}/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestPart(value = "nickname", required = false) String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태가 아닙니다");
        }

        // 내 계정이 아닌 다른 사람의 프로필 수정 방지
        if (!id.equals(userDetails.getUserEntity().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("권한이 없습니다");
        }

        ProfileDTO updated = profileService.updateProfile(id, nickname, profileImage);
        return ResponseEntity.ok(updated);
    }
}
