package com.example.thisplay.common.moviepage.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.friend.service.FriendshipService;
import com.example.thisplay.common.moviepage.DTO.Movie_FolderDTO;
import com.example.thisplay.common.moviepage.DTO.movie_saveDTO;
import com.example.thisplay.common.moviepage.DTO.MovieDTO;
import com.example.thisplay.common.rec_list.DTO.ViewFolderDTO;
import com.example.thisplay.common.rec_list.entity.FolderVisibility;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final MovieFolderRepository folderRepository;
    private final TmdbApiClient tmdbApiClient;
    private final FriendshipService friendshipService;

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
    public ViewFolderDTO getMoviesByFolder(Long folderId, UserEntity requester) {
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        UserEntity owner = folder.getUser();

        // ✅ PRIVATE → 본인만
        if (folder.getVisibility() == FolderVisibility.PRIVATE &&
                !owner.getUserId().equals(requester.getUserId())) {
            throw new RuntimeException("이 폴더는 비공개입니다.");
        }
        // 0️⃣ 폴더 주인은 무조건 접근 가능
        if (owner.getUserId().equals(requester.getUserId())) {
            return mapToViewFolderDTO(folder);
        }

        // ✅ FRIENDS → 친구만
        if (folder.getVisibility() == FolderVisibility.FRIENDS &&
                !friendshipService.areFriends(requester, owner)) {
            throw new RuntimeException("이 폴더는 친구에게만 공개됩니다.");
        }

        return mapToViewFolderDTO(folder);
    }

    private ViewFolderDTO mapToViewFolderDTO(MovieFolder folder) {
        List<MovieDTO> movies = folder.getMovies().stream().map(m -> {
            return new MovieDTO(
                    m.getTmdbId(),
                    m.getTitle(),
                    m.getOriginalTitle(),
                    m.getPosterPath()
            );
        }).collect(Collectors.toList());

        return new ViewFolderDTO(
                folder.getId(),
                folder.getName(),
                folder.getVisibility(), // 새로 추가한 필드
                movies
        );
    }

    // 폴더 내 단일 영화 삭제
    public String deleteMovieFromFolder(Long folderId, int tmdbId, UserEntity loginUser) {
        // 1️⃣ 폴더 존재 여부 확인
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        // 2️⃣ 폴더 소유자 검증
        if (!Objects.equals(folder.getUser().getNickname(), loginUser.getNickname())) {
            throw new RuntimeException("해당 폴더에 접근 권한이 없습니다.");
        }

        // 3️⃣ 폴더 내 영화 존재 여부 확인
        MovieEntity movie = movieRepository.findByTmdbIdAndFolder_Id(tmdbId, folderId)
                .orElseThrow(() -> new RuntimeException("해당 영화가 폴더에 없습니다."));

        // 4️⃣ 삭제 수행
        movieRepository.delete(movie);

        // 5️⃣ 결과 메시지 반환
        return String.format("폴더 '%s'에서 영화 '%s'가 삭제되었습니다.", folder.getName(), movie.getTitle());
    }


}
