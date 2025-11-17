package com.example.thisplay.common.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OneLineReviewDTO {

    private Long reviewId;
    private int movieId;

    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private int likeCount;

    private Date createdAt;
    private String oneLineReview;
}
