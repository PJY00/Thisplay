package com.example.thisplay.common.rec_list.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.repository.MovieFolderRepository;
import com.example.thisplay.common.rec_list.repository.MovieRepository;
import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieFolderRepository folderRepository;
    private final TmdbApiClient tmdbApiClient;

    // 영화 저장
    public MovieEntity saveMovie(Long folderId, int tmdbId, UserEntity loginUser) {
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더 없음"));

        // ✅ 폴더 소유자 검증
        if (!folder.getUser().getUserId().equals(loginUser.getUserId())) {
            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
        }

        // TMDB에서 영화 상세 정보 가져오기 (블로킹 방식 사용, Mono -> block())
        JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();

        MovieEntity movie = MovieEntity.builder()
                .tmdbId(tmdbId)
                .title(movieDetail.path("title").asText())
                .originalTitle(movieDetail.path("original_title").asText())
                .posterPath(movieDetail.path("poster_path").asText())
                .folder(folder)
                .build();

        return movieRepository.save(movie);
    }

    // 폴더별 영화 리스트 조회
    public List<MovieEntity> getMoviesByFolder(Long folderId, Long userId) {
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더 없음"));
        // ✅ 폴더 소유자 검증
        if (!folder.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
        }
        return movieRepository.findAllByFolder(folder);
    }
}
