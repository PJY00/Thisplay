package com.example.thisplay.global.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TmdbApiClient {

    private final WebClient webClient;

    public TmdbApiClient(@Value("${tmdb.api.base-url}") String baseUrl,
                         @Value("${tmdb.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, apiKey)
                .build();
    }

    // 장르 조회
    public Mono<JsonNode> getGenres() {
        return webClient.get()
                .uri("/genre/movie/list?language=ko-KR")
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    // 특정 장르 영화 조회
    public Mono<JsonNode> getMoviesByGenre(int genreId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("with_genres", genreId)
                        .queryParam("sort_by", "popularity.desc")
                        .queryParam("language", "ko-KR")
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
