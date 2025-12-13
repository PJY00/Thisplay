package com.example.thisplay.common.friend.dto;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.friend.entity.Friendship;
import com.example.thisplay.common.friend.entity.FriendshipStatus;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FriendDTO {

    private Long friendshipId;      // 친구 관계 ID
    private Long otherUserId;       // 상대방 ID
    private String otherUserName;   // 상대방 닉네임
    private FriendshipStatus status;// 관계 상태
    private boolean isFrom;         // 내가 보낸 요청이면 true
    private String nickname; // ❌ 이거 하나면 누가 보냈는지 구분 불가
    private String profileImgUrl;

    public static FriendDTO fromEntity(Friendship friendship, Long loginUserId) {
        FriendDTO dto = new FriendDTO();
        dto.setFriendshipId(friendship.getFriendshipId());
        dto.setStatus(friendship.getStatus());

        UserEntity sender = friendship.getSendUser();
        UserEntity receiver = friendship.getReceiveUser();

        // is from 계산
        boolean isFrom = sender.getUserId().equals(loginUserId);
        dto.setFrom(isFrom);

        // 상대방 정보 세팅 (내가 sender면 receiver가 상대, 반대면 sender가 상대)
        UserEntity other = isFrom ? receiver : sender;
        dto.setOtherUserId(other.getUserId());
        dto.setOtherUserName(other.getNickname());
        dto.setProfileImgUrl(other.getProfileImgUrl());

        return dto;
    }
}