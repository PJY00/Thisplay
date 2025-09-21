package com.example.thisplay.common.Auth.repository;

import com.example.thisplay.common.Auth.Entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    // 영화별 리뷰 조회
    List<ReviewEntity> findByMovieId(int movieId);

    // 유저별 리뷰 조회
    List<ReviewEntity> findByUser_UserId(Long userId);
}