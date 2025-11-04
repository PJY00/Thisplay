package com.example.thisplay.common.setting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDTO {
    private Long Id;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
