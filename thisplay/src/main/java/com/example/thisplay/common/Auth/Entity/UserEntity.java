package com.example.thisplay.common.Auth.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="user_table")

public class UserEntity {
    @Id
    @GeneratedValue
    private Long userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;
}