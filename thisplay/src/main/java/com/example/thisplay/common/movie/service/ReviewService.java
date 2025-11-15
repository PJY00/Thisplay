package com.example.thisplay.common.movie.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.dto.OneLineReviewDTO;
import com.example.thisplay.common.movie.dto.ReviewDTO;
import com.example.thisplay.common.movie.entity.ReviewEntity;
import com.example.thisplay.common.movie.repository.ReviewRepository;
import com.example.thisplay.common.moviepage.DTO.Movie_FolderDTO;
import com.example.thisplay.common.moviepage.DTO.movie_saveDTO;
import com.example.thisplay.common.rec_list.entity.FolderVisibility;
import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.repository.MovieFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieFolderRepository folderRepository;
    private final MovieFolderRepository movieFolderRepository;


    // 전체 리뷰 조회
    @Transactional
    public List<ReviewDTO> getAllReviews() {
        List<ReviewEntity> reviewEntityList = reviewRepository.findAll();
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        for (ReviewEntity reviewEntity : reviewEntityList) {
            reviewDTOList.add(ReviewDTO.toReviewDTO(reviewEntity));
        }
        return reviewDTOList;
    }

    // 단일 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewDTO getReview(Long id) {
        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + id));
        return ReviewDTO.toReviewDTO(reviewEntity);
    }

    @Transactional
    public ReviewDTO getReviewAndIncrease(Long id, Long currentUserId) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + id));

        boolean isAuthor = currentUserId != null && review.getUser().getUserId().equals(currentUserId);

        if (!isAuthor) {
            reviewRepository.incrementViewCount(id); // DB에서 +1
            // flush 이후 최신값 반영 위해 다시 조회
            review = reviewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + id));
        }

        return ReviewDTO.toReviewDTO(review);
    }

    // 영화별 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByMovie(int movieId) {
        List<ReviewEntity> reviewEntityList = reviewRepository.findByMovieId(movieId);
        List<ReviewDTO> dtoList = new ArrayList<>();

        for (ReviewEntity reviewEntity : reviewEntityList) {
            dtoList.add(ReviewDTO.toReviewDTO(reviewEntity));
        }

        return dtoList;
    }

    // 유저별 리뷰 조회
    @Transactional
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        List<ReviewEntity> reviewEntityList = reviewRepository.findByUser_UserId(userId);
        List<ReviewDTO> dtoList = new ArrayList<>();

        for (ReviewEntity reviewEntity : reviewEntityList) {
            dtoList.add(ReviewDTO.toReviewDTO(reviewEntity));
        }
        return dtoList;
    }

    //리뷰 등록
    @Transactional
    public ReviewDTO create(ReviewDTO dto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        ReviewEntity entity = ReviewEntity.toCreateEntity(dto, user);
        ReviewEntity saved = reviewRepository.save(entity);

        return ReviewDTO.toReviewDTO(saved);
    }

    // 리뷰 수정
    @Transactional
    public ReviewDTO update(ReviewDTO dto, Long currentUserId) {
        ReviewEntity review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + dto.getReviewId()));

        if (!review.getUser().getUserId().equals(currentUserId)) {
            throw new SecurityException("권한이 없습니다");
        }

        if (dto.getReviewTitle() != null) review.setReviewTitle(dto.getReviewTitle());
        if (dto.getReviewBody() != null) review.setReviewBody(dto.getReviewBody());
        if (dto.getOneLineReview() != null) review.setOneLineReview(dto.getOneLineReview());
        if (dto.getStar() != 0) review.setStar(dto.getStar());

        ReviewEntity updated = reviewRepository.save(review);
        return ReviewDTO.toReviewDTO(updated);
    }

    // 리뷰 삭제
    @Transactional
    public void delete(Long reviewId, Long currentUserId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다. id=" + reviewId));

        if (!review.getUser().getUserId().equals(currentUserId)) {
            throw new SecurityException("권한이 없습니다");
        }

        reviewRepository.delete(review);
    }

    // 페이징
    @Transactional(readOnly = true)
    public Page<ReviewDTO> paging(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(ReviewDTO::toReviewDTO);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByMoviePaging(int movieId, Pageable pageable) {
        return reviewRepository.findByMovieId(movieId, pageable)
                .map(ReviewDTO::toReviewDTO);
    }

    // 전체 폴더
    @Transactional(readOnly = true)
    public List<movie_saveDTO> getMyFoldersMovies(UserEntity viewer) {
        UserEntity persistentUser = userRepository.findByNickname(viewer.getNickname())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));


        List<MovieFolder> folders = folderRepository.findAllByUser(persistentUser);
        List<movie_saveDTO> result = new ArrayList<>();


        for (MovieFolder folder : folders) {

            Movie_FolderDTO folderDTO = new Movie_FolderDTO(
                    folder.getId(),
                    folder.getName()
            );

            for (MovieEntity movie : folder.getMovies()) {
                movie_saveDTO movieDTO = new movie_saveDTO(
                        movie.getTmdbId(),
                        movie.getTitle(),
                        movie.getOriginalTitle(),
                        movie.getPosterPath(),
                        folderDTO
                );

                result.add(movieDTO);
            }
        }

        return result;
    }

    //단일폴더
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getMyFolderReviewsPaging(Long folderId,
                                                    UserEntity viewer,
                                                    Pageable pageable) {

        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다. id=" + folderId));

        if (folder.getVisibility() == FolderVisibility.PRIVATE &&
                !Objects.equals(folder.getUser().getUserId(), viewer.getUserId())) {
            throw new AccessDeniedException("폴더 접근 불가");
        }

        // 폴더 안 영화 tmdbId 목록
        List<Integer> tmdbIds = folder.getMovies().stream()
                .map(MovieEntity::getTmdbId)
                .toList();

        if (tmdbIds.isEmpty()) {
            return Page.empty(pageable);
        }

        return reviewRepository
                .findByUser_UserIdAndMovieIdIn(viewer.getUserId(), tmdbIds, pageable)
                .map(ReviewDTO::toReviewDTO);
    }

    //한줄리뷰조회
    public Page<OneLineReviewDTO> getOneLineReviewsByMovie(int movieId, Pageable pageable) {

        Page<ReviewEntity> reviewPage = reviewRepository.findByMovieId(movieId, pageable);

        return reviewPage.map(review -> {
            UserEntity user = review.getUser();

            return OneLineReviewDTO.builder()
                    .reviewId(review.getReviewId())
                    .movieId(review.getMovieId())              // int
                    .userId(user.getUserId())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImgUrl())
                    .createdAt(review.getCreatedAt())
                    .oneLineReview(review.getOneLineReview())
                    .build();
        });
    }
}
