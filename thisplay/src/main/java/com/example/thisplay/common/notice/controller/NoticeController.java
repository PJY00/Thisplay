package com.example.thisplay.common.notice.controller;

import com.example.thisplay.common.notice.dto.NoticeDTO;
import com.example.thisplay.common.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setting/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @GetMapping
    public List<NoticeDTO> getNotices() {
        return noticeService.getAllNotices();
    }

    // 공지사항 상세 조회
    @GetMapping("/{noticeId}")
    public NoticeDTO getNotice(@PathVariable Long noticeId) {
        return noticeService.getNoticeById(noticeId);
    }

    // 공지사항 등록
    @PostMapping
    public NoticeDTO createNotice(@RequestBody NoticeDTO dto) {
        return noticeService.createNotice(dto);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public NoticeDTO updateNotice(@PathVariable Long noticeId, @RequestBody NoticeDTO dto) {
        return noticeService.updateNotice(noticeId, dto);
    }

    // 공지사항 삭제 (추가 가능)
    @DeleteMapping("/{noticeId}")
    public String deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return "공지사항이 삭제되었습니다.";
    }
}
