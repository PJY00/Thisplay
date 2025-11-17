package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.movie.dto.CommentDTO;
import com.example.thisplay.common.movie.dto.CommentResponseDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews/{reviewId}/comments") // 리뷰 기준 라우팅
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /** 댓글 작성 */
    @PostMapping
    public ResponseEntity<?> addComment(
            @PathVariable Long reviewId,
            @RequestBody CommentDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        UserEntity user = userDetails.getUserEntity();
        CommentEntity saved = commentService.createComment(user, reviewId, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("commentId", saved.getCommentId());
        response.put("reviewId", saved.getReview().getReviewId());
        response.put("createdAt", saved.getCreatedAt());
        response.put("content", saved.getContent());

        return ResponseEntity.created(
                URI.create("/api/reviews/" + reviewId + "/comments/" + saved.getCommentId())
        ).body(response);
    }

   //댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReview(reviewId));
    }

  //댓글수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long reviewId,
            @PathVariable Long commentId,
            @RequestBody CommentDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        CommentResponseDTO res =
                commentService.updateComment(commentId, userDetails.getUserEntity(), dto.getContent());
        return ResponseEntity.ok(res);
    }

    //댓글삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long reviewId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        commentService.deleteComment(commentId, userDetails.getUserEntity());
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}
