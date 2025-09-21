package com.example.thisplay.common.main.repository;

import com.example.thisplay.common.main.entity.GenreSelection;
import org.springframework.data.jpa.repository.JpaRepository;

//필요 없으면 Entity 부분과 함께 지우기
public interface GenreSelectionRepository extends JpaRepository<GenreSelection,Long> {
}
