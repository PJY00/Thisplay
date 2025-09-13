package com.example.thisplay.common.movie.DTO;

import lombok.Data;

import java.util.List;

@Data
public class MovieDetailDTO {
    private int id;                 // 영화 고유 id
    private String title;           // 한국 제목
    private String originalTitle;   // 원제목
    private String overview;        // 줄거리
    private String releaseDate;     // 한국 개봉일
    private List<String> genres;    // 장르
    private String posterUrl;       // 포스터 URL
    private Integer runtime;        // 상영 시간
    private List<CastInfo> cast;    // 배우 정보
    private String certification;   // 심의 등급

    @Data
    public static class CastInfo {
        private String name;       // 배우 이름
        private String character;  // 배역 이름
    }
}
