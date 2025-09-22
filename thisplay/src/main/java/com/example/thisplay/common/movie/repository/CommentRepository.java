package com.example.thisplay.common.movie.repository;

import com.example.thisplay.common.movie.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByMovieId(Long movieId);
}
