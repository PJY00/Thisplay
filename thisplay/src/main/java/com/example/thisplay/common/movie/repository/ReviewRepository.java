package com.example.thisplay.common.movie.repository;

import com.example.thisplay.common.movie.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByMovieId(int movieId);// 영화별 리뷰 조회
    Page<ReviewEntity> findByMovieId(int movieId, Pageable pageable);//영화별 목록
    List<ReviewEntity> findByUser_UserId(Long userId);// 유저별 리뷰 조회
}