package com.example.thisplay.global.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
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
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
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

    // 특정 영화 상세 조회 (credits, release_dates 포함)
    public Mono<JsonNode> getMovieDetail(int movieId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{id}")
                        .queryParam("api_key", apiKey)
                        .queryParam("language", "ko-KR")
                        .queryParam("append_to_response", "credits,release_dates")
                        .build(movieId))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode obj = mapper.createObjectNode();

                    // 기본 정보
                    obj.put("id", response.path("id").asInt());
                    obj.put("title", response.path("title").asText(""));
                    obj.put("original_title", response.path("original_title").asText(""));
                    obj.put("overview", response.path("overview").asText(""));
                    obj.put("runtime", response.path("runtime").asInt(0));

                    // TMDB 평점 정보 추가
                    obj.put("vote_average", response.path("vote_average").asDouble(0.0));
                    obj.put("vote_count", response.path("vote_count").asInt(0));

                    // 포스터 URL
                    String posterPath = response.hasNonNull("poster_path") ? response.get("poster_path").asText() : "";
                    obj.put("poster_path", "https://image.tmdb.org/t/p/w185" + posterPath);

                    // 장르 (배열 -> 문자열 배열)
                    ArrayNode genreArray = mapper.createArrayNode();
                    response.withArray("genres").forEach(genre -> {
                        genreArray.add(genre.get("name").asText());
                    });
                    obj.set("genres", genreArray);

                    // 배우 정보 (상위 5명)
                    ArrayNode castArray = mapper.createArrayNode();
                    ArrayNode casts = (ArrayNode) response.path("credits").path("cast");
                    for (int i = 0; i < Math.min(10, casts.size()); i++) {
                        JsonNode cast = casts.get(i);
                        ObjectNode castObj = mapper.createObjectNode();
                        castObj.put("name", cast.path("name").asText(""));
                        castObj.put("character", cast.path("character").asText(""));
                        String profilePath = cast.hasNonNull("profile_path") ? cast.get("profile_path").asText() : "";
                        castObj.put("profile_path", profilePath.isEmpty() ? "" : "https://image.tmdb.org/t/p/w185" + profilePath);
                        castArray.add(castObj);
                    }
                    obj.set("cast", castArray);

                    // 한국 개봉일 & 심의 등급
                    String releaseDate = "";
                    String certification = "";
                    ArrayNode releaseResults = (ArrayNode) response.path("release_dates").path("results");
                    for (JsonNode country : releaseResults) {
                        if ("KR".equals(country.path("iso_3166_1").asText())) {
                            ArrayNode krReleases = (ArrayNode) country.path("release_dates");
                            if (krReleases.size() > 0) {
                                JsonNode krRelease = krReleases.get(0);
                                releaseDate = krRelease.path("release_date").asText("");
                                certification = krRelease.path("certification").asText("");
                            }
                        }
                    }
                    obj.put("release_date", releaseDate);
                    obj.put("certification", certification);

                    return obj;
                });
    }

    // 영화 제목 자동완성 (검색)
    // 현재 첫 번째 요청시 한글 query를 webclient가 제대로 인코딩 하지 못해 TMDB서버가 400을 반환하는 오류가 있음.
    //이거 일차적으로 해결 했는데 여전히 가끔씩 발행: TMDB가 글자 길이가 짧으면 잘 인식을 못 한다고 함...
    public Mono<JsonNode> searchMovies(String query) {
        return webClient.get()
                .uri(uriBuilder -> {
                    String uri = uriBuilder
                            .path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "ko-KR")
                            .queryParam("query", query)
                            .queryParam("include_adult", false)
                            .build()
                            .toString();
                    // 강제로 URI Encoding
                    return UriComponentsBuilder.fromUriString(uri).encode().build().toUri();
                })
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayNode results = (ArrayNode) response.get("results");
                    ArrayNode filtered = mapper.createArrayNode();

                    for (JsonNode movie : results) {
                        String title = movie.path("title").asText("").trim();

                        // 🔹 title이 query로 시작하는지 확인 (대소문자 구분 X)
                        if (title.startsWith(query)) {
                            ObjectNode obj = mapper.createObjectNode();
                            obj.put("id", movie.path("id").asInt());
                            obj.put("title", title);  // 한국어 제목
                            obj.put("original_title", movie.path("original_title").asText(""));

                            // poster_path (전체 URL 만들기)
                            String posterPath = movie.hasNonNull("poster_path")
                                    ? movie.get("poster_path").asText()
                                    : "";
                            obj.put("poster_path",
                                    posterPath.isEmpty()
                                            ? ""
                                            : "https://image.tmdb.org/t/p/w185" + posterPath);
                            filtered.add(obj);

                            // 최대 5개까지만
                            if (filtered.size() >= 5) break;
                        }
                    }

                    return filtered;
                });
    }

}
