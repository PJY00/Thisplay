package com.example.thisplay.common.moviepage;

import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.service.MovieService;
import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final TmdbApiClient tmdbApiClient;
    private final MovieService movieService;

    // TMDB 영화 상세 정보 조회
    @GetMapping("/show/{movieId}")
    public Mono<JsonNode> getMovieDetail(@PathVariable int movieId){
        return tmdbApiClient.getMovieDetail(movieId);
    }

    // 영화 DB에 저장
    @PostMapping("/save/{folderId}/{tmdbId}")
    public MovieEntity saveMovie(@PathVariable Long folderId, @PathVariable int tmdbId) {
        return movieService.saveMovie(folderId, tmdbId);
    }

    // 폴더 내 영화 리스트 조회
    @GetMapping("/folder/{folderId}")
    public List<MovieEntity> getMoviesByFolder(@PathVariable Long folderId) {
        return movieService.getMoviesByFolder(folderId);
    }
}
