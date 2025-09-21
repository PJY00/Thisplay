package com.example.thisplay.common.rec_list.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.repository.MovieFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieFolderService {
    private final MovieFolderRepository folderRepository;

    // 특정 유저의 폴더 리스트 조회
    public List<MovieFolder> getFoldersByUser(UserEntity user) {
        return folderRepository.findAllByUser(user);
    }

    // 새로운 폴더 생성
    public MovieFolder createFolder(UserEntity user, String folderName) {
        MovieFolder folder = MovieFolder.builder()
                .name(folderName)
                .user(user)
                .build();
        return folderRepository.save(folder);
    }
}
