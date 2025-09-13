package com.example.thisplay.common.main.service;

import com.example.thisplay.global.api.TmdbApiClient;
import com.example.thisplay.common.main.entity.GenreSelection;
import com.example.thisplay.common.main.repository.GenreSelectionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final TmdbApiClient tmdbApiClient;
    private final GenreSelectionRepository genreRepo;

    // 장르 선택 저장
    public GenreSelection saveGenre(int genreId) {
        GenreSelection selection = GenreSelection.builder()
                .genreId(genreId)
                .build();
        return genreRepo.save(selection);
    }

    // 저장된 장르 기반 인기 영화 20개 조회
    public Mono<JsonNode> getMoviesFromSavedGenre() {
        GenreSelection selection = genreRepo.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("장르가 저장되지 않았습니다."));

        return tmdbApiClient.getMoviesByGenre(selection.getGenreId());
    }
}
