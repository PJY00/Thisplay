package com.example.thisplay.common.movie.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.movie.dto.CommentDTO;
import com.example.thisplay.common.movie.entity.CommentEntity;
import com.example.thisplay.common.movie.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository; //DB 조회용 추가

    public CommentEntity createComment(UserEntity user, Long movieId, CommentDTO dto) {
        // JWTFilter가 만든 임시 UserEntity → DB에서 영속 UserEntity로 교체
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        CommentEntity comment = CommentEntity.builder()
                .user(persistentUser)   // 영속 엔티티 연결
                .movieId(movieId)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
}
