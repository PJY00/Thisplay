package com.example.thisplay.common.friend.repository;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.friend.entity.Friendship;
import com.example.thisplay.common.friend.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findBySendUserAndReceiveUser(UserEntity sendUser, UserEntity receiveUser);

    List<Friendship> findBySendUser(UserEntity sendUser);

    List<Friendship> findByReceiveUser(UserEntity receiveUser);

    List<Friendship> findBySendUserAndStatus(UserEntity sendUser, FriendshipStatus status);

    List<Friendship> findByReceiveUserAndStatus(UserEntity receiveUser, FriendshipStatus status);

    // 두 유저 사이에 수락 상태의 친구 관계가 존재하는지 확인
    Optional<Friendship> findBySendUserAndReceiveUserAndStatus(
            UserEntity sendUser,
            UserEntity receiveUser,
            FriendshipStatus status
    );

    // 친구 관계는 양방향이라, 역방향도 확인
    Optional<Friendship> findByReceiveUserAndSendUserAndStatus(
            UserEntity receiveUser,
            UserEntity sendUser,
            FriendshipStatus status
    );
}

