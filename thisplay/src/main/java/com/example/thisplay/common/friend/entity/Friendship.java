package com.example.thisplay.common.friend.entity;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name="friendship_table")
public class Friendship {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long friendshipId;

    //친구 요청을 보낸 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_user_id")
    private UserEntity send_user;

    //친구 요청을 받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receive_user_id")
    private UserEntity receive_user;

    //친구 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    //양방향 연관관계 설정 보조 메서드
    public void acceptFriendshipRequest(){
        this.status=FriendshipStatus.ACCEPTED;
    }

}
