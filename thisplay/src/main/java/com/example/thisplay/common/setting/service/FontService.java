package com.example.thisplay.common.setting.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.setting.dto.FontRequestDto;
import com.example.thisplay.common.setting.entity.FontEntity;
import com.example.thisplay.common.setting.entity.FontEntity.FontSize;
import com.example.thisplay.common.setting.repository.FontRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FontService {

    private final FontRepository fontRepository;

    public String updateFontSize(UserEntity user, FontRequestDto requestDto) {

        //  폰트 설정이 없으면 자동 생성
        FontEntity fontEntity = fontRepository.findByUser(user)
                .orElseGet(() -> {
                    FontEntity newSetting = FontEntity.builder()
                            .user(user)
                            .fontSize(FontSize.MIDDLE)
                            .build();
                    return fontRepository.save(newSetting);
                });

        //  소문자, 대문자 모두 처리
        FontSize newFontSize;
        try {
            newFontSize = FontSize.valueOf(requestDto.getFontSize().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("fontSize는 small, middle, large 중 하나여야 합니다.");
        }

        fontEntity.setFontSize(newFontSize);
        fontRepository.save(fontEntity);

        return "글꼴 크기가 변경되었습니다.";
    }
}
