package com.example.thisplay.common.Auth.Entity;

import com.example.thisplay.common.rec_list.entity.MovieFolder;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String name;

    @Column(nullable = true)
    private String password;

    @Column
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;

    // 유저가 가진 폴더 리스트 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieFolder> folders = new ArrayList<>();
}