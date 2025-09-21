package com.example.thisplay.common.rec_list.controller;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import com.example.thisplay.common.rec_list.service.MovieFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {
    private final MovieFolderService folderService;

    // 특정 유저의 폴더 리스트 조회
    @GetMapping("/user/{userId}")
    public List<MovieFolder> getFoldersByUser(@PathVariable Long userId){
        UserEntity user = new UserEntity();
        user.setUserId(userId); // 간단하게 userId만 셋팅, 실제로는 Authentication에서 가져오는 게 안전
        return folderService.getFoldersByUser(user);
    }

    // 폴더 생성
    //쿼리 스트링 형태로 ?folderName=어쩌구 로 post주면 새로운 폴더 생성
    @PostMapping("/create/{userId}")
    public MovieFolder createFolder(@PathVariable Long userId, @RequestParam String folderName){
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        return folderService.createFolder(user, folderName);
    }
}
