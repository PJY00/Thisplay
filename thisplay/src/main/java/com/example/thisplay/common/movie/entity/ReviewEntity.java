package com.example.thisplay.common.movie.entity;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column
    private int movieId;
    private String movieTitle;

    @Column(length=50, nullable = false)
    private String reviewTitle;

    @Column(nullable = false)
    private String reviewBody;

    @Column(length = 100, nullable = false)
    private String oneLineReview;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int star;
    private int seen_arrange;
    private int likeCount;
    private int commentCount;

    @OneToMany(mappedBy = "review")
    @Builder.Default
    private List<LikeEntity> likes = new ArrayList<>();


    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public static ReviewEntity toCreateEntity(ReviewDTO dto, UserEntity user) {
        return ReviewEntity.builder()
                .user(user)
                .movieId(dto.getMovieId())
                .movieTitle(dto.getMovieTitle())
                .reviewTitle(dto.getReviewTitle())
                .reviewBody(dto.getReviewBody())
                .oneLineReview(dto.getOneLineReview())
                .star(dto.getStar())
                .seen_arrange(0)
                .likeCount(0)
                .commentCount(0)
                .build();
    }





}