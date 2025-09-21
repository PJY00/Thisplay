package com.example.thisplay.common.Auth.Entity;

import com.example.thisplay.common.Auth.DTO.ReviewDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="review_table")
public class ReviewEntity {
    @Id
    @GeneratedValue
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String reviewTitle;

    @Column(length=500, nullable = false)
    private String reviewBody;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int commentCount;
    private int likeCount;
    private int star;
    private int seen_arrange;

    @Column
    private int movieId; // 지금은 String, 나중에 MovieEntity로 연결 가능

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public static ReviewEntity toCreateEntity(ReviewDTO dto, UserEntity user) {
        return ReviewEntity.builder()
                .user(user)
                .reviewTitle(dto.getReviewTitle())
                .reviewBody(dto.getReviewBody())
                .star(dto.getStar())
                .movieId(dto.getMovieId())
                .seen_arrange(0)
                .likeCount(0)
                .commentCount(0)
                .build();
    }





}