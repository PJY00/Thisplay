package com.example.thisplay.common.notice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {
    private Long noticeId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
}
