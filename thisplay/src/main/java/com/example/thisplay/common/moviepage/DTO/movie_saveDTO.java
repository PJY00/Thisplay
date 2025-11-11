package com.example.thisplay.common.moviepage.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class movie_saveDTO {
    private int tmdbId;
    private String title;
    private String originalTitle;
    private String posterPath;

    private Movie_FolderDTO folder; // folder id와 name만 포함
}
