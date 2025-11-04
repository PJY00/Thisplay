package com.example.thisplay.common.setting.controller;

import com.example.thisplay.common.setting.dto.FaqDTO;
import com.example.thisplay.common.setting.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setting/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    // 전체 조회 (모두 접근)
    @GetMapping
    public List<FaqDTO> list() {
        return faqService.findAll();
    }

    // 단건 조회 (모두 접근)
    @GetMapping("/{id}")
    public FaqDTO get(@PathVariable Long id) {
        return faqService.findOne(id);
    }

    // 생성 (관리자만)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public FaqDTO create(@RequestBody /*@Valid*/ FaqDTO dto) {
        return faqService.create(dto);
    }

    // 수정 (관리자만)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FaqDTO update(@PathVariable Long id, @RequestBody /*@Valid*/ FaqDTO dto) {
        return faqService.update(id, dto);
    }

    // 삭제 (관리자만)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        faqService.delete(id);
    }
}

