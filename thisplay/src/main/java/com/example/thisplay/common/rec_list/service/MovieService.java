package com.example.thisplay.common.rec_list.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.moviepage.DTO.Movie_FolderDTO;
import com.example.thisplay.common.moviepage.DTO.movie_saveDTO;
import com.example.thisplay.common.moviepage.exception.FolderAccessDeniedException;
import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.repository.MovieFolderRepository;
import com.example.thisplay.common.rec_list.repository.MovieRepository;
import com.example.thisplay.global.api.TmdbApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieFolderRepository folderRepository;
    private final TmdbApiClient tmdbApiClient;

    // 영화 저장
    public Optional<movie_saveDTO> saveMovie(Long folderId, int tmdbId, UserEntity loginUser) {
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더 없음"));

        // ✅ 폴더 소유자 검증
        if (!Objects.equals(folder.getUser().getNickname(), loginUser.getNickname())) {
            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
        }

        // ✅ 중복 TMDB ID 체크
        movieRepository.findByTmdbIdAndFolder_Id(tmdbId, folderId)
                .ifPresent(m -> {
                    throw new RuntimeException("이미 존재하는 영화입니다.");
                });

        // TMDB에서 영화 상세 정보 가져오기 (블로킹 방식 사용, Mono -> block())
        JsonNode movieDetail = tmdbApiClient.getMovieDetail(tmdbId).block();

        MovieEntity movie = MovieEntity.builder()
                .tmdbId(tmdbId)
                .title(movieDetail.path("title").asText())
                .originalTitle(movieDetail.path("original_title").asText())
                .posterPath(movieDetail.path("poster_path").asText())
                .folder(folder)
                .build();
        movie=movieRepository.save(movie);
        // DTO 변환
        Movie_FolderDTO folderDTO = new Movie_FolderDTO(folder.getId(),folder.getName());

        movie_saveDTO dto=new movie_saveDTO(
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getPosterPath(),
                folderDTO
        );
        return Optional.of(dto);
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
