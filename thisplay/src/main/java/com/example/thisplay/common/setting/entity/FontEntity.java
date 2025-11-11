package com.example.thisplay.common.setting.entity;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "font_settings")
public class FontEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 글꼴 크기 (SMALL, MIDDLE, LARGE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FontSize fontSize;

    // 로그인된 사용자와 1:1 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public enum FontSize {
        SMALL, MIDDLE, LARGE
    }
}
