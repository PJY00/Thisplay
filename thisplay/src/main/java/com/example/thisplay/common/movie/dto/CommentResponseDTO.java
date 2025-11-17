package com.example.thisplay.common.movie.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private Long reviewId;
    private String content;
    private LocalDateTime createdAt;
    private String writer;
    private Long writerId;
}
