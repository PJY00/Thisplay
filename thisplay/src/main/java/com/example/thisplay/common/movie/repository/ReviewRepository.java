package com.example.thisplay.common.movie.repository;

import com.example.thisplay.common.movie.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByMovieId(int tmdbId); // 영화별 리뷰 조회
    Page<ReviewEntity> findByMovieId(int tmdbId, Pageable pageable); // 페이징
    List<ReviewEntity> findByUser_UserId(Long userId); // 유저별 리뷰 조회
    List<ReviewEntity> findByMovieIdIn(List<Integer> movieIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ReviewEntity r set r.seen_arrange = r.seen_arrange + 1 where r.reviewId = :id")
    int incrementViewCount(@Param("id") Long id);


}