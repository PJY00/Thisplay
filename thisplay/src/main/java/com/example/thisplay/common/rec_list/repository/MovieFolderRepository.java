package com.example.thisplay.common.rec_list.repository;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieFolderRepository extends JpaRepository<MovieFolder, Long> {
    List<MovieFolder> findAllByUser(UserEntity user);
}
