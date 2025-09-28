package com.example.thisplay.common.rec_list.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.DTO.FolderDTO;
import com.example.thisplay.common.rec_list.DTO.ViewFolderDTO;
import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.service.MovieFolderService;
import com.example.thisplay.common.rec_list.service.MovieService;
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


    // 특정 유저의 폴더 리스트 조회
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

    // 폴더 내 영화 리스트 조회
    @GetMapping("/{folderId}/movies")
    public ViewFolderDTO getMoviesByFolder(@PathVariable Long folderId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserEntity user = userDetails.getUserEntity();
        return movieService.getMoviesByFolder(folderId, user);
    }
}
