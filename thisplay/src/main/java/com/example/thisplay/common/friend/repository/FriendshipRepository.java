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
}

