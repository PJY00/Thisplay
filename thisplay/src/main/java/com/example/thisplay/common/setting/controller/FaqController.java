package com.example.thisplay.common.setting.controller;

import com.example.thisplay.common.setting.dto.FaqDTO;
import com.example.thisplay.common.setting.dto.NoticeDTO;
import com.example.thisplay.common.setting.service.FaqService;
import com.example.thisplay.common.setting.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/setting/faq")
@RequiredArgsConstructor
public class FaqController {
    private final FaqService faqService;

    // 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public FaqDTO createFaq(@RequestBody FaqDTO dto) {
        return faqService.createFaq(dto);
    }

    // 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{faqId}")
    public FaqDTO updateFaq(@PathVariable Long faqId, @RequestBody FaqDTO dto) {
        return faqService.updateFaq(faqId, dto);
    }

    // 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{faqId}")
    public String deleteFaq(@PathVariable Long faqId) {
        faqService.deleteFaq(faqId);
        return "FAQ가 삭제되었습니다.";
    }

    //faq조회
    @GetMapping
    public List<FaqDTO> getFaqs() {
        return faqService.getAllFaqs();
    }

    //특정 faq조회
    @GetMapping("/{faqId}")
    public FaqDTO getFaq(@PathVariable Long faqId) {
        return faqService.getFaqById(faqId);
    }
}

