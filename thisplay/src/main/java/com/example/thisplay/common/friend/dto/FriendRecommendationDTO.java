package com.example.thisplay.common.friend.dto;

import com.example.thisplay.common.Auth.Entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter@AllArgsConstructor
public class FriendRecommendationDTO {
    private Long userId;
    private String nickname;
    private String profileImgUrl;
    private UserStatus status;
}
