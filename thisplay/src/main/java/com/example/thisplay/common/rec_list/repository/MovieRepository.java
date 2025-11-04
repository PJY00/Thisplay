package com.example.thisplay.common.rec_list.repository;

import com.example.thisplay.common.rec_list.entity.MovieEntity;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends CrudRepository<MovieEntity,Long> {
    List<MovieEntity> findAllByFolder(MovieFolder folder);
    Optional<MovieEntity> findByTmdbIdAndFolder_Id(int tmdbId, Long folderId);
}
