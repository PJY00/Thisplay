package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.common.movie.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    // 단일 리뷰 조회
    @GetMapping("/{id}")
    public ReviewDTO getReview(@PathVariable Long id) {
        return reviewService.getReview(id);
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
    public ReviewDTO create(@RequestBody ReviewDTO dto) {
        return reviewService.create(dto);
    } //등록


    @PutMapping("/{id}")
    public ReviewDTO update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        dto.setReviewId(id);
        return reviewService.update(dto);
    }// 수정

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        reviewService.delete(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "삭제 완료");
        response.put("deletedId", id);
        return response;
    } //삭제

    @GetMapping("/paging")
    public Page<ReviewDTO> paging(@PageableDefault(page = 1) Pageable pageable) {
        return reviewService.paging(pageable);
    } //페이징
}
