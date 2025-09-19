package com.example.thisplay.common.Auth.Controller;

import com.example.thisplay.common.Auth.DTO.ProfileDTO;
import com.example.thisplay.common.Auth.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    // 프로필 조회
    @GetMapping("/{id}/profile")
    public ProfileDTO getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    // 프로필 수정
    @PutMapping("/{id}/profile")
    public ProfileDTO updateProfile(
            @PathVariable Long id,
            @RequestBody ProfileDTO dto
    ) {
        return profileService.updateProfile(id, dto);
    }
}