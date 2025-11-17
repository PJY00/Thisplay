package com.example.thisplay.common.rec_list.repository;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieFolderRepository extends JpaRepository<MovieFolder, Long> {
    List<MovieFolder> findAllByUser(UserEntity user);

    //폴더 내 영화들의 tmdb리스트
    @Query("""
        select distinct m.tmdbId
        from MovieFolder f
        join f.movies m
        where f.id = :folderId
    """)
    List<Integer> findTmdbIdsInFolder(@Param("folderId") Long folderId);

    // 내 폴더(또는 일부 폴더)의 영화들의 tmdbId 리스트
    @Query("""
        select distinct m.tmdbId
        from MovieFolder f
        join f.movies m
        where f.user.userId = :ownerId
          and (:folderIds is null or f.id in :folderIds)
    """)
    List<Integer> findTmdbIdsInMyFolders(@Param("ownerId") Long ownerId,
                                         @Param("folderIds") List<Long> folderIds);
}
