package com.example.thisplay.common.rec_list.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.DTO.FolderDTO;
import com.example.thisplay.common.rec_list.DTO.ViewFolderDTO;
import com.example.thisplay.common.rec_list.service.MovieFolderService;
import com.example.thisplay.common.moviepage.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {
    private final MovieFolderService folderService;
    private final MovieService movieService;
    //권한 (공개범위)나누어서 특정 유저 폴더 리스트 허가를 해야 함.
    //전체 공개 리스트 설정.
    //enum을 달아서 1은 개인, 2는 친구, 3은 전체공개로 해서 3은 접근성(id검사)검사 하지 말고...?

    // 내 폴더 리스트 조회
    // 현재는 타인이 접근하면 그냥 빈 리스트 반환함
    @GetMapping("/me")
    public List<ViewFolderDTO> getMyFolders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        return folderService.getFoldersByUser(user);
    }

    // 폴더 생성
    //쿼리 스트링 형태로 ?folderName=어쩌구 로 post주면 새로운 폴더 생성
    @PostMapping("/create")
    public FolderDTO createFolder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestParam String folderName) {
        UserEntity user = userDetails.getUserEntity();
        return folderService.createFolder(user, folderName);
    }

    // 폴더 내 영화 리스트 조회-> 폴더 ID말고 폴더 이름으로 할까 고민중
    @GetMapping("/{folderId}/movies")
    public ViewFolderDTO getMoviesByFolder(@PathVariable Long folderId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        return movieService.getMoviesByFolder(folderId, user);
    }

    //폴더 삭제 기능
    @DeleteMapping("/{folderId}")
    public String deleteFolder(@PathVariable Long folderId,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        folderService.deleteFolder(folderId, user);
        return "폴더 및 해당 폴더 내 영화가 성공적으로 삭제되었습니다.";
    }
}
