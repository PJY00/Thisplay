package com.example.thisplay.common.setting.controller;

import com.example.thisplay.common.setting.dto.NoticeDTO;
import com.example.thisplay.common.setting.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setting/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public NoticeDTO createNotice(@RequestBody NoticeDTO dto) {
        return noticeService.createNotice(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{noticeId}")
    public NoticeDTO updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDTO dto) {
        return noticeService.updateNotice(noticeId, dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{noticeId}")
    public String deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return "공지사항이 삭제되었습니다.";
    }

    // 조회는 누구나 가능
    @GetMapping
    public List<NoticeDTO> getNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/{noticeId}")
    public NoticeDTO getNotice(@PathVariable Long noticeId) {
        return noticeService.getNoticeById(noticeId);
    }
}

