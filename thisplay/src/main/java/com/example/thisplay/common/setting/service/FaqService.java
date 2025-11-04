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

@Service
@RequiredArgsConstructor
@Transactional
public class FaqService {
    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FaqDTO> findAll() {
        return faqRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public FaqDTO findOne(Long id) {
        FaqEntity e = faqRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("FAQ not found: " + id));
        return toDto(e);
    }

    public FaqDTO create(FaqDTO dto) {
        FaqEntity e = FaqEntity.builder()
                .question(dto.getQuestion())
                .answer(dto.getAnswer())
                .build();
        faqRepository.save(e);
        return toDto(e);
    }

    public FaqDTO update(Long id, FaqDTO dto) {
        FaqEntity e = faqRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException("FAQ not found: " + id));
        e.setQuestion(dto.getQuestion());
        e.setAnswer(dto.getAnswer());
        // updatedAt은 JPA Auditing이 자동 반영
        return toDto(e);
    }

    public void delete(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException("FAQ not found: " + id);
        }
        faqRepository.deleteById(id);
    }

    /* --------- mapper --------- */
    private FaqDTO toDto(FaqEntity e) {
        return FaqDTO.builder()
                .id(e.getId())
                .question(e.getQuestion())
                .answer(e.getAnswer())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
