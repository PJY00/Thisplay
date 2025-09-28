package com.example.thisplay.common.moviepage.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.moviepage.DTO.Movie_FolderDTO;
import com.example.thisplay.common.moviepage.DTO.movie_saveDTO;
import com.example.thisplay.common.moviepage.DTO.MovieDTO;
import com.example.thisplay.common.rec_list.DTO.ViewFolderDTO;
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

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieFolderRepository folderRepository;
    private final TmdbApiClient tmdbApiClient;

    // 영화 저장
    public movie_saveDTO saveMovie(Long folderId, int tmdbId, UserEntity loginUser) {
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

        return new movie_saveDTO(
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getPosterPath(),
                folderDTO
        );
    }

    // 폴더별 영화 리스트 조회
    public ViewFolderDTO getMoviesByFolder(Long folderId, UserEntity userNickname) {
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더 없음"));
        // ✅ 폴더 소유자 검증
        if (!Objects.equals(folder.getUser().getNickname(), userNickname.getNickname())) {
            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
        }
        // MovieEntity → MovieDTO 변환
        List<MovieDTO> movieDTOs = folder.getMovies().stream()
                .map(entity -> new MovieDTO(
                        entity.getTmdbId(),
                        entity.getTitle(),
                        entity.getOriginalTitle(),
                        entity.getPosterPath()
                ))
                .toList();

        // ViewFolderDTO 생성
        return new ViewFolderDTO(
                folder.getId(),
                folder.getName(),
                movieDTOs
        );
    }
}
