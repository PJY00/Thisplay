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
    public ResponseEntity<?> addLike(@PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }
        Long userId = userDetails.getUserId();

        if (likeService.isReviewWriter(userId, reviewId)) {
            return ResponseEntity.status(403).body("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }
        likeService.addLike(userId, reviewId);
        return ResponseEntity.ok("좋아요 등록 완료");
    }


    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> removeLike(@PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Long userId = userDetails.getUserId();

        if (!likeService.isLikeOwner(userId, reviewId)) {
            return ResponseEntity.status(403).body("본인이 누른 좋아요만 취소할 수 있습니다.");
        }

        likeService.removeLike(userId, reviewId);
        return ResponseEntity.ok("좋아요 취소 완료");
}


@GetMapping("/{reviewId}")
public ResponseEntity<LikeDTO> getLikes(@PathVariable Long reviewId) {
    return ResponseEntity.ok(likeService.getLikes(reviewId));
}
}

