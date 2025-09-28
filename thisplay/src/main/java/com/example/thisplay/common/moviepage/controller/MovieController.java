package com.example.thisplay.common.moviepage.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.moviepage.DTO.movie_saveDTO;
import com.example.thisplay.common.moviepage.service.MovieService;
import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public movie_saveDTO saveMovie(@PathVariable Long folderId,
                                   @PathVariable int tmdbId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity loginUser = userDetails.getUserEntity();
        return movieService.saveMovie(folderId, tmdbId, loginUser);
    }
}
