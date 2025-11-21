package com.example.thisplay.common.Auth.Entity;

import com.example.thisplay.common.friend.entity.Friendship;
import com.example.thisplay.common.rec_list.entity.MovieFolder;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
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

    @Column(nullable = true, unique = true)
    private String googleId;

    @Column(nullable = true, unique = true)
    private String email;

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

    @Column(name = "profile_img_url")
    @Builder.Default
    private String profileImgUrl = "/images/profile/default.png";

    //유저의 상태(추천친구 or 일반 유저 구분 시 사용)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status=UserStatus.NORMAL;

    // 유저가 가진 폴더 리스트 (1:N)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieFolder> folders = new ArrayList<>();

    // ✅ 유저가 포함된 친구 관계 리스트 (1:N)
    @OneToMany(mappedBy = "sendUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendshipList = new ArrayList<>();

    @OneToMany(mappedBy = "receiveUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> receivedFriendships = new ArrayList<>();

    @CreationTimestamp
    private Date createdAt;


}