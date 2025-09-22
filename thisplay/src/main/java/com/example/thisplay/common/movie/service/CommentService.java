package com.example.thisplay.common.movie.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.controller.CommentResponseDTO;
import com.example.thisplay.common.movie.dto.CommentDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository; //DB 조회용 추가

    public CommentEntity createComment(UserEntity user, Long movieId, CommentDTO dto) {
        // JWTFilter가 만든 임시 UserEntity → DB에서 영속 UserEntity로 교체
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        CommentEntity comment = CommentEntity.builder()
                .user(persistentUser)   // 영속 엔티티 연결
                .movieId(movieId)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
    public List<CommentResponseDTO> getCommentsByMovie(Long movieId) {
        List<CommentEntity> comments = commentRepository.findByMovieId(movieId);

        return comments.stream()
                .map(c -> CommentResponseDTO.builder()
                        .commentId(c.getCommentId())
                        .movieId(c.getMovieId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .writer(c.getUser().getNickname())
                        .build()
                ).toList();
    }

    public CommentResponseDTO updateComment(Long commentId, UserEntity user, String newContent) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // DB에서 영속 유저 다시 조회
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if (!comment.getUser().getUserId().equals(persistentUser.getUserId())) {
            throw new SecurityException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(newContent);
        CommentEntity updated = commentRepository.save(comment);

        return CommentResponseDTO.builder()
                .commentId(updated.getCommentId())
                .movieId(updated.getMovieId())
                .content(updated.getContent())
                .createdAt(updated.getCreatedAt())
                .writer(updated.getUser().getNickname())
                .build();
    }


    public void deleteComment(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        // DB에서 영속 UserEntity 다시 조회
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 본인 댓글인지 확인
        if (!comment.getUser().getUserId().equals(persistentUser.getUserId())) {
            throw new SecurityException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

}
