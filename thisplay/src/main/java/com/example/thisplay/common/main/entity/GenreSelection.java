package com.example.thisplay.common.main.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Builder
public class GenreSelection {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private int genreId;
}
