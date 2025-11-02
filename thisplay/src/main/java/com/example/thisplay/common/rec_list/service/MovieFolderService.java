package com.example.thisplay.common.rec_list.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.rec_list.DTO.FolderDTO;
import com.example.thisplay.common.moviepage.DTO.MovieDTO;
import com.example.thisplay.common.rec_list.DTO.ViewFolderDTO;
import com.example.thisplay.common.rec_list.entity.FolderVisibility;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.repository.MovieFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieFolderService {
    private final MovieFolderRepository folderRepository;
    private final UserRepository userRepository;

    // 특정 유저의 폴더 리스트 조회
    public List<ViewFolderDTO> getFoldersByUser(UserEntity user) {
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        List<MovieFolder> folders = folderRepository.findAllByUser(persistentUser);

        return folders.stream().map(folder -> {
            ViewFolderDTO dto = new ViewFolderDTO();
            dto.setFolderId(folder.getId());
            dto.setFolderName(folder.getName());
            dto.setVisibility(folder.getVisibility().name()); // 👈 추가됨

            List<MovieDTO> movieList = folder.getMovies().stream().map(m -> {
                MovieDTO movieDTO = new MovieDTO();
                movieDTO.setTmdbId(m.getTmdbId());
                movieDTO.setTitle(m.getTitle());
                movieDTO.setOriginalTitle(m.getOriginalTitle());
                movieDTO.setPosterPath(m.getPosterPath());
                return movieDTO;
            }).collect(Collectors.toList());

            dto.setMovies(movieList);
            return dto;
        }).collect(Collectors.toList());
    }

    // 새로운 폴더 생성
    public FolderDTO createFolder(UserEntity user, String folderName, FolderVisibility visibility) {
        // DB에서 영속화된 User 가져오기
        UserEntity User = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        MovieFolder folder = MovieFolder.builder()
                .name(folderName)
                .user(User)
                .visibility(visibility!=null?visibility:FolderVisibility.PRIVATE)
                .build();
        MovieFolder savedFolder = folderRepository.save(folder);

        // DTO로 변환 후 반환
        return new FolderDTO(
                savedFolder.getId(),
                savedFolder.getName(),
                savedFolder.getUser().getNickname(),
                savedFolder.getVisibility().name()
        );
    }

    // 폴더 삭제
    public void deleteFolder(Long folderId, UserEntity user) {
        // 1. 유저 확인 (DB에 존재하는 유저인지)
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 2. 폴더 가져오기
        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        // 3. 해당 폴더가 로그인한 유저의 소유인지 확인(타인은 할 수 없게)
        if (!folder.getUser().getUserId().equals(persistentUser.getUserId())) {
            throw new RuntimeException("이 폴더를 삭제할 권한이 없습니다.");
        }

        // 4. 삭제 (CascadeType.ALL + orphanRemoval로 인해 폴더 안 영화도 같이 삭제됨)
        folderRepository.delete(folder);
    }

    //공개 범위 변경 메서드 추가
    public void updateFolderVisibility(Long folderId, FolderVisibility newVisibility, UserEntity user) {
        UserEntity persistentUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        MovieFolder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

        if (!folder.getUser().getUserId().equals(persistentUser.getUserId())) {
            throw new RuntimeException("이 폴더의 공개 범위를 변경할 권한이 없습니다.");
        }

        folder.setVisibility(newVisibility);
        folderRepository.save(folder);
    }
}
