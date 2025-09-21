package com.example.thisplay.common.Auth.service;

import com.example.thisplay.common.Auth.DTO.ReviewDTO;
import com.example.thisplay.common.Auth.Entity.ReviewEntity;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.ReviewRepository;
import com.example.thisplay.common.Auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

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

    // 리뷰 등록
    @Transactional
    public ReviewDTO create(ReviewDTO dto) {
        UserEntity user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + dto.getUserId()));

        ReviewEntity entity = ReviewEntity.toCreateEntity(dto, user);
        ReviewEntity saved = reviewRepository.save(entity);
        return ReviewDTO.toReviewDTO(saved);
    }

    // 리뷰 수정
    @Transactional
    public ReviewDTO update(ReviewDTO dto) {
        ReviewEntity reviewentity = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + dto.getReviewId()));

        reviewentity.setReviewTitle(dto.getReviewTitle());
        reviewentity.setReviewBody(dto.getReviewBody());
        reviewentity.setStar(dto.getStar());

        ReviewEntity updated = reviewRepository.save(reviewentity);
        return ReviewDTO.toReviewDTO(updated);
    }

    // 리뷰 삭제
    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

        // 페이징
    @Transactional(readOnly = true)
    public Page<ReviewDTO> paging(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(ReviewDTO::toReviewDTO); // Page 매핑에도 static 메서드 활용
    }


}
