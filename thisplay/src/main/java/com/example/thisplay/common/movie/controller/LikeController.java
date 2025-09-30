package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.movie.dto.LikeDTO;
import com.example.thisplay.common.movie.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{reviewId}")
    public ResponseEntity<?> addLike(@PathVariable Long reviewId,
                                     @RequestParam Long userId) {
        likeService.addLike(userId, reviewId);
        return ResponseEntity.ok("좋아요 등록 완료");
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> removeLike(@PathVariable Long reviewId,
                                        @RequestParam Long userId) {
        likeService.removeLike(userId, reviewId);
        return ResponseEntity.ok("좋아요 취소 완료");
    }


    @GetMapping("/{reviewId}")
    public ResponseEntity<LikeDTO> getLikes(@PathVariable Long reviewId) {
        return ResponseEntity.ok(likeService.getLikes(reviewId));
    }
}