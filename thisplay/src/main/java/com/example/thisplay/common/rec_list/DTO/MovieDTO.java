package com.example.thisplay.common.rec_list.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private int tmdbId;

    private String title;
    private String originalTitle;
    private String posterPath;
}
