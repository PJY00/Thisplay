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

    private Long reviewId;          // 리뷰 ID
    private int movieId;           // 영화 ID

    private Long userId;            // 사용자 ID
    private String nickname;        // 사용자 닉네임
    private String profileImageUrl; // 프로필 사진 URL

    private Date createdAt; // 리뷰 작성 날짜
    private String oneLineReview;    // 한줄 리뷰
}
