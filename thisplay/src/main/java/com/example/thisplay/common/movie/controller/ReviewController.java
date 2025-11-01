package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.common.movie.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // 전체 리뷰 조회
    @GetMapping
    public List<ReviewDTO> getAllReviews() {
        return reviewService.getAllReviews();
    }

    // 상세 리뷰 조회 + 조회수 증가
    @GetMapping("/{id}")
    public ReviewDTO getReview(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = (userDetails != null)
                ? userDetails.getUserEntity().getUserId()
                : null;
        return reviewService.getReviewAndIncrease(id, currentUserId);
    }

    // 영화별 리뷰 조회
    @GetMapping("/movie/{movieId}")
    public List<ReviewDTO> getByMovie(@PathVariable int movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }

    // 유저별 리뷰 조회
    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @PostMapping
    public ReviewDTO create(@RequestBody ReviewDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setUserId(userDetails.getUserEntity().getUserId());
        return reviewService.create(dto, userDetails.getUserId());
    }

    @PatchMapping("/{id}")
    public ReviewDTO patchReview(@PathVariable Long id, @RequestBody ReviewDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setReviewId(id);
        return reviewService.update(dto, userDetails.getUserId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.delete(id, userDetails.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }

    @GetMapping("/paging")
    public Page<ReviewDTO> paging(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.paging(pageable);
    }

    @GetMapping("/movie/{movieId}/paging")
    public Page<ReviewDTO> getByMoviePaging(@PathVariable int movieId, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.getReviewsByMoviePaging(movieId, pageable);
    }

}
