package com.example.thisplay.common.movie.dto;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDTO {
    private int likeCount;
    private List<UserInfo> users;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfo{
        private Long userId;
        private String nickname;
    }
}