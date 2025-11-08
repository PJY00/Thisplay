package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.common.movie.service.ReviewService;
import com.example.thisplay.global.api.TmdbApiClient;
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

    // 리뷰 상세 조회
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
        String movieTitle = fetchMovieTitle(tmdbId);

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

    // 리뷰 작성
    @PostMapping("/movie/{tmdbId}")
    public ReviewDTO create(@PathVariable int tmdbId,
                            @RequestBody ReviewDTO dto,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setUserId(userDetails.getUserEntity().getUserId());
        dto.setMovieId(tmdbId);
        dto.setMovieTitle(fetchMovieTitle(tmdbId)); // 영화 제목 자동 세팅

        return reviewService.create(dto, userDetails.getUserId());
    }

    // 리뷰 수정
    @PatchMapping("/{id}")
    public ReviewDTO patchReview(@PathVariable Long id,
                                 @RequestBody ReviewDTO dto,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setReviewId(id);
        return reviewService.update(dto, userDetails.getUserId());
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.delete(id, userDetails.getUserId());
        return ResponseEntity.ok("삭제 완료");
    }

    // 전체 페이징 조회
    @GetMapping("/paging")
    public Page<ReviewDTO> paging(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return reviewService.paging(pageable);
    }

    // 영화별 리뷰 페이징
    @GetMapping("/movie/{tmdbId}/paging")
    public Map<String, Object> getByMoviePaging(
            @PathVariable int tmdbId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ReviewDTO> page = reviewService.getReviewsByMoviePaging(tmdbId, pageable);
        String movieTitle = fetchMovieTitle(tmdbId);

        Map<String, Object> response = new HashMap<>();
        response.put("tmdbId", tmdbId);
        response.put("movieTitle", movieTitle);
        response.put("page", page);

        return response;
    }

    //내부 공용 메서드: TMDB 영화 제목 가져오기
    private String fetchMovieTitle(int tmdbId) {
        try {
            JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();
            return movieDetail.path("title").asText("제목 없음");
        } catch (Exception e) {
            return "제목 없음";
        }
    }

    //단일폴더리뷰
    @GetMapping("/myfolders/{folderId}")
    public List<ReviewDTO> getMyFolderReviews(
            @PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw new RuntimeException("로그인이 필요합니다");
        return reviewService.getMyFolderReviews(folderId, userDetails.getUserEntity());
    }

    //전체폴더리뷰
    @GetMapping("/myfolders")
    public List<ReviewDTO> getMyFoldersReviews(
            @RequestParam(required = false) List<Long> ids,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw new RuntimeException("로그인이 필요합니다");
        return reviewService.getMyFoldersReviews(userDetails.getUserEntity(), ids);
    }
}
