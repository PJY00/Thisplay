package com.example.thisplay.common.Auth.DTO;


import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private Date createdAt;
}
