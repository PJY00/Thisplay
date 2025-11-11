package com.example.thisplay.common.setting.service;

import com.example.thisplay.common.setting.dto.FaqDTO;
import com.example.thisplay.common.setting.entity.FaqEntity;
import com.example.thisplay.common.setting.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    // 전체 조회
    public List<FaqDTO> getAllFaqs() {
        return faqRepository.findAll().stream()
                .map(f -> new FaqDTO(
                        f.getId(),
                        f.getQuestion(),
                        f.getAnswer(),
                        f.getCreatedAt(),
                        f.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // 특정 faq 조회
    public FaqDTO getFaqById(Long id) {
        FaqEntity f = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));
        return new FaqDTO(
                f.getId(),
                f.getQuestion(),
                f.getAnswer(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }

    // 등록
    public FaqDTO createFaq(FaqDTO dto) {
        FaqEntity f = FaqEntity.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .build();

        FaqEntity saved = faqRepository.save(f);
        return new FaqDTO(
                saved.getId(),
                saved.getQuestion(),
                saved.getAnswer(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    // 수정
    public FaqDTO updateFaq(Long id, FaqDTO dto) {
        FaqEntity f = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ를 찾을 수 없습니다."));

        if (dto.getQuestion() != null) f.setQuestion(dto.getQuestion());
        if (dto.getAnswer() != null) f.setAnswer(dto.getAnswer());

        FaqEntity updated = faqRepository.save(f);
        return new FaqDTO(
                updated.getId(),
                updated.getQuestion(),
                updated.getAnswer(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }

    // 삭제
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }
}
