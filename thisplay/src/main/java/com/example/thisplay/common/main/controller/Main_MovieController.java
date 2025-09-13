package com.example.thisplay.common.main.controller;

import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class Main_MovieController {
    private final TmdbApiClient tmdbApiClient;

    //tmdb장르 리스트 가져오기
    @GetMapping("/genres")
    public Mono<JsonNode> getGenres(){
        return tmdbApiClient.getGenres();
    }

    // 특정 장르 top20 인기 영화
    @GetMapping("/genres/{genreId}/top20")
    public Mono<JsonNode> getMoviesByGenre(@PathVariable int genreId) {
        return tmdbApiClient.getMoviesByGenre(genreId);
    }
}
