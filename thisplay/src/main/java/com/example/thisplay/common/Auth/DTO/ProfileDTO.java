package com.example.thisplay.common.Auth.DTO;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Long userId;
    private String nickname;
    private String profileImgUrl;
}
