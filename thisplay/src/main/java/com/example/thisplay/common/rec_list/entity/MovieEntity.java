package com.example.thisplay.common.rec_list.entity;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tmdbId; // TMDB 영화 ID
    private String title;
    private String originalTitle;
    private String posterPath;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private MovieFolder folder;
}
