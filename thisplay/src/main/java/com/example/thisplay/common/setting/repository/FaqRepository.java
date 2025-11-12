package com.example.thisplay.common.setting.repository;

import com.example.thisplay.common.setting.entity.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntity, Long> {
}