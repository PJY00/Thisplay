package com.example.thisplay.common.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
}
