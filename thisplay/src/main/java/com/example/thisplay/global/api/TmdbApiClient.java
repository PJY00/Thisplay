package com.example.thisplay.global.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TmdbApiClient {

    private final String apiKey;
    private final WebClient webClient;

    public TmdbApiClient(@Value("${tmdb.api.base-url}") String baseUrl,
                         @Value("${tmdb.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // 1️⃣ 장르 목록 조회
    public Mono<JsonNode> getGenres() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/genre/movie/list")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .build())
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
                        .queryParam("page", 1)//TMDB는 기본적으로 한 페이지당 20개의 영화를 반환함.->40개를 받고 싶으면 따로 처리를 해줘야 함.
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    ArrayNode results = (ArrayNode) response.get("results"); // 원래 20개 영화
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayNode filtered = mapper.createArrayNode(); // 새 배열 생성

                    for (JsonNode movie : results) {
                        ObjectNode obj = mapper.createObjectNode();
                        obj.put("id", movie.get("id").asInt());
                        obj.put("title", movie.get("title").asText());
                        // genre_ids는 ArrayNode 그대로 복사
                        obj.set("genre_ids", movie.get("genre_ids"));
                        // poster_path를 TMDB 이미지 URL로 변환
                        String posterPath = movie.hasNonNull("poster_path") ? movie.get("poster_path").asText() : "";
                        obj.put("poster_path", "https://image.tmdb.org/t/p/w185" + posterPath);

                        filtered.add(obj);
                    }
                    return filtered;
                });
    }
}
