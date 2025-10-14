package com.example.thisplay.common.movie.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.dto.LikeDTO;
import com.example.thisplay.common.movie.entity.LikeEntity;
import com.example.thisplay.common.movie.entity.ReviewEntity;
import com.example.thisplay.common.movie.repository.LikeRepository;
import com.example.thisplay.common.movie.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean isReviewWriter(Long userId, Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));
        return review.getUser().getUserId().equals(userId);
    }

    @Transactional(readOnly = true)
    public boolean isLikeOwner(Long userId, Long reviewId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));
        return likeRepository.existsByReviewAndUser(review, user);
    }

    @Transactional
    public void addLike(Long userId, Long reviewId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));

        if (likeRepository.existsByReviewAndUser(review, user)) {
            throw new RuntimeException("이미 좋아요 누른 상태");
        }

        LikeEntity like = LikeEntity.builder()
                .user(user)
                .review(review)
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(Long userId, Long reviewId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));

        LikeEntity like = likeRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new RuntimeException("좋아요 기록 없음"));

        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    public LikeDTO getLikes(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰 없음"));

        List<LikeDTO.UserInfo> users = likeRepository.findByReview(review).stream()
                .map(like -> LikeDTO.UserInfo.builder()
                        .userId(like.getUser().getUserId())
                        .nickname(like.getUser().getNickname())
                        .build())
                .toList();


        return LikeDTO.builder()
                .likeCount(users.size())
                .users(users)
                .build();
    }
}

