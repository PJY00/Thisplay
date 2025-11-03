package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.global.api.TmdbApiClient;
import com.example.thisplay.common.movie.service.ReviewService;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final TmdbApiClient tmdbApiClient;

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
    @GetMapping("/movie/{tmdbId}")
    public Map<String, Object> getByMovie(@PathVariable int tmdbId) {

        List<ReviewDTO> reviews = reviewService.getReviewsByMovie(tmdbId);

        JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();
        String movieTitle = movieDetail.path("title").asText("제목 없음");

        Map<String, Object> response = new HashMap<>();
        response.put("tmdbId", tmdbId);
        response.put("movieTitle", movieTitle);
        response.put("reviews", reviews);

        return response;
    }

    // 유저별 리뷰 조회
    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @PostMapping("/movie/{tmdbId}")
    public ReviewDTO create(@PathVariable int tmdbId, @RequestBody ReviewDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setUserId(userDetails.getUserEntity().getUserId());
        dto.setMovieId(tmdbId);
        JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();
        dto.setMovieTitle(movieDetail.path("title").asText("제목 없음"));
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

    @GetMapping("/movie/{tmdbId}/paging")
    public Map<String, Object> getByMoviePaging(
            @PathVariable int tmdbId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewDTO> page = reviewService.getReviewsByMoviePaging(tmdbId, pageable);

        JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();
        String movieTitle = movieDetail.path("title").asText("제목 없음");

        Map<String, Object> response = new HashMap<>();
        response.put("tmdbId", tmdbId);
        response.put("movieTitle", movieTitle);
        response.put("page", page);

        return response;
    }

}
