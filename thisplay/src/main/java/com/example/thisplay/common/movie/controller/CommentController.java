package com.example.thisplay.common.movie.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.movie.dto.CommentDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{movieId}/comments")
    public Map<String, Object> addComment(
            @PathVariable Long movieId,
            @RequestBody CommentDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        CommentEntity saved = commentService.createComment(user, movieId, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("commentId", saved.getCommentId());
        response.put("movieId", saved.getMovieId());
        response.put("createdAt", saved.getCreatedAt());
        response.put("content", saved.getContent());
        return response;
    }
}
