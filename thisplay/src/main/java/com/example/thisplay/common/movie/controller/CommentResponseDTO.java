package com.example.thisplay.common.movie.controller;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private Long movieId;
    private String content;
    private LocalDateTime createdAt;
    private String writer;
}
