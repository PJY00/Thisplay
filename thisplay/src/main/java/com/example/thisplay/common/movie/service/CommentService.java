package com.example.thisplay.common.movie.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.dto.CommentDTO;
import com.example.thisplay.common.movie.dto.CommentResponseDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.entity.ReviewEntity;
import com.example.thisplay.common.movie.repository.CommentRepository;
import com.example.thisplay.common.movie.repository.ReviewRepository;
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
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;


    public CommentEntity createComment(UserEntity loginUser, Long reviewId, CommentDTO dto) {
        // 가능하면 userId로 영속 유저 조회
        UserEntity user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

        CommentEntity comment = CommentEntity.builder()
                .user(user)
                .review(review)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        CommentEntity saved = commentRepository.save(comment);

        // commentCount 증가
        review.setCommentCount(review.getCommentCount() + 1);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByReview(Long reviewId) {
        List<CommentEntity> comments = commentRepository.findByReview_ReviewId(reviewId);

        return comments.stream()
                .map(c -> CommentResponseDTO.builder()
                        .commentId(c.getCommentId())
                        .reviewId(c.getReview().getReviewId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .writer(c.getUser().getNickname())
                        .writerId(c.getUser().getUserId())
                        .build()
                ).toList();
    }

//댓글수정
    public CommentResponseDTO updateComment(Long commentId, UserEntity loginUser, String newContent) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        UserEntity user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(newContent);
        CommentEntity updated = commentRepository.save(comment);

        return CommentResponseDTO.builder()
                .commentId(updated.getCommentId())
                .reviewId(updated.getReview().getReviewId())
                .content(updated.getContent())
                .createdAt(updated.getCreatedAt())
                .writer(updated.getUser().getNickname())
                .writerId(updated.getUser().getUserId())
                .build();
    }


    public void deleteComment(Long commentId, UserEntity loginUser) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(loginUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        Long reviewId = comment.getReview().getReviewId();
        commentRepository.delete(comment);


        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));
        review.setCommentCount(Math.max(0, review.getCommentCount() - 1));
    }
}
