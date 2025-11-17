package com.example.thisplay.common.movie.dto;

import com.example.thisplay.common.movie.entity.ReviewEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {
    private Long reviewId;

    private Long userId;
    private int movieId; //리뷰등록시필요
    private String movieTitle;
    private String writer;


    private String reviewTitle;
    private String reviewBody;
    private String oneLineReview;
    private int star;
    private int seenArrange;
    private int likeCount;
    private int commentCount;
    private Date createdAt;
    private Date updatedAt;


    public static ReviewDTO toReviewDTO(ReviewEntity entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(entity.getReviewId());
        dto.setReviewTitle(entity.getReviewTitle());
        dto.setReviewBody(entity.getReviewBody());
        dto.setOneLineReview(entity.getOneLineReview());
        dto.setMovieId(entity.getMovieId());
        dto.setMovieTitle(entity.getMovieTitle());
        dto.setStar(entity.getStar());
        dto.setCommentCount(entity.getCommentCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setSeenArrange(entity.getSeen_arrange());
        dto.setWriter(entity.getUser().getNickname());
        dto.setUserId(entity.getUser().getUserId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

}