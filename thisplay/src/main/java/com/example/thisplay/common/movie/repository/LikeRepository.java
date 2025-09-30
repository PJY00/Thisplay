package com.example.thisplay.common.movie.repository;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.movie.entity.LikeEntity;
import com.example.thisplay.common.movie.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    long countByReview(ReviewEntity review);
    boolean existsByReviewAndUser(ReviewEntity review, UserEntity user);
    Optional<LikeEntity> findByReviewAndUser(ReviewEntity review, UserEntity user);
    List<LikeEntity> findByReview(ReviewEntity review);
}