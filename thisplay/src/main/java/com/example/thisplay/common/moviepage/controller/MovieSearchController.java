package com.example.thisplay.common.moviepage.controller;

import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieSearchController {
    private final TmdbApiClient tmdbApiClient;

    // 자동완성용 영화 검색 API
    @GetMapping("/search")
    public Mono<ResponseEntity<JsonNode>> searchMovies(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return tmdbApiClient.searchMovies(query)
                .map(results -> ResponseEntity.ok().body(results))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.internalServerError().body(null));
                });
    }
}
