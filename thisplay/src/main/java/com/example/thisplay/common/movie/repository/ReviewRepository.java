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

    List<ReviewEntity> findByMovieId(int tmdbId);
    Page<ReviewEntity> findByMovieId(int tmdbId, Pageable pageable);
    List<ReviewEntity> findByUser_UserId(Long userId);
    List<ReviewEntity> findByUser_UserIdAndMovieIdIn(Long userId, List<Integer> movieIds);

    Page<ReviewEntity> findByUser_UserIdAndMovieIdIn(Long userId,
                                                     List<Integer> movieIds,
                                                     Pageable pageable);

    @Query("SELECT DISTINCT r.movieId " +
            "FROM ReviewEntity r " +
            "WHERE r.user.userId = :userId " +
            "AND r.movieId IN :movieIds")
    List<Integer> findReviewedMovieIds(@Param("userId") Long userId,
                                       @Param("movieIds") List<Integer> movieIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ReviewEntity r set r.seen_arrange = r.seen_arrange + 1 where r.reviewId = :id")
    int incrementViewCount(@Param("id") Long id);
}