package com.example.thisplay.common.movie;

import com.example.thisplay.common.movie.DTO.MovieDetailDTO;
import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movies/show")
@RequiredArgsConstructor
public class MovieController {
    private final TmdbApiClient tmdbApiClient;

    @GetMapping("/{movieId}")
    public Mono<JsonNode> getMovieDetail(@PathVariable int movieId){
        return tmdbApiClient.getMovieDetail(movieId);
    }
}
