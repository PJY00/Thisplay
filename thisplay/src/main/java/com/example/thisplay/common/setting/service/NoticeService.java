package com.example.thisplay.common.setting.service;

import com.example.thisplay.common.setting.dto.NoticeDTO;
import com.example.thisplay.common.setting.entity.NoticeEntity;
import com.example.thisplay.common.setting.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeDTO> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(n -> new NoticeDTO(
                        n.getNoticeId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getCreatedAt().toString(),
                        n.getUpdatedAt() != null ? n.getUpdatedAt().toString() : null))
                .collect(Collectors.toList());
    }

    public NoticeDTO getNoticeById(Long id) {
        NoticeEntity n = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        return new NoticeDTO(n.getNoticeId(), n.getTitle(), n.getContent(),
                n.getCreatedAt().toString(), n.getUpdatedAt() != null ? n.getUpdatedAt().toString() : null);
    }

    public NoticeDTO createNotice(NoticeDTO dto) {
        NoticeEntity n = NoticeEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        NoticeEntity saved = noticeRepository.save(n);
        return new NoticeDTO(saved.getNoticeId(), saved.getTitle(), saved.getContent(),
                saved.getCreatedAt().toString(), null);
    }

    public NoticeDTO updateNotice(Long id, NoticeDTO dto) {
        NoticeEntity n = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        n.setTitle(dto.getTitle());
        n.setContent(dto.getContent());
        NoticeEntity updated = noticeRepository.save(n);
        return new NoticeDTO(updated.getNoticeId(), updated.getTitle(), updated.getContent(),
                updated.getCreatedAt().toString(), updated.getUpdatedAt() != null ? updated.getUpdatedAt().toString() : null);
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}
