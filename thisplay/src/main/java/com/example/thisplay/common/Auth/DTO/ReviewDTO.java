package com.example.thisplay.common.Auth.DTO;

import com.example.thisplay.common.Auth.Entity.ReviewEntity;
import lombok.*;

import java.time.LocalDateTime;
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
    private String writer;
//    private String movieName; 얘 넣을까요 말까요


    private String reviewTitle;
    private String reviewBody;
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
        dto.setMovieId(entity.getMovieId());
        dto.setStar(entity.getStar());
        dto.setSeenArrange(entity.getSeen_arrange());
        dto.setWriter(entity.getUser().getNickname());
        dto.setUserId(entity.getUser().getUserId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

}