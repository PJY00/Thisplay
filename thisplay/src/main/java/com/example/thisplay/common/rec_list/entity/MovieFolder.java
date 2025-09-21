package com.example.thisplay.common.rec_list.entity;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 폴더 이름

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // 폴더 소유자

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieEntity> movies = new ArrayList<>();
}
