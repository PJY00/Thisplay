package com.example.thisplay.common.friend.service;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.Entity.UserStatus;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.example.thisplay.common.friend.dto.FriendDTO;
import com.example.thisplay.common.friend.dto.FriendRecommendationDTO;
import com.example.thisplay.common.friend.dto.FriendSearchDTO;
import com.example.thisplay.common.friend.entity.Friendship;
import com.example.thisplay.common.friend.entity.FriendshipStatus;
import com.example.thisplay.common.friend.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    // ✅ 1. 친구 요청
    public String sendFriendRequest(Long senderId, String receiverNickname) {
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("요청자 유저 없음"));

        UserEntity receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new RuntimeException("받는 유저 없음"));

        // 자기 자신에게 요청 불가
        if (sender.getUserId().equals(receiver.getUserId())) {
            throw new RuntimeException("자기 자신에게는 친구 요청 불가");
        }

        // 중복 요청 방지
        Optional<Friendship> existing = friendshipRepository.findBySendUserAndReceiveUser(sender, receiver);
        if (existing.isPresent()) {
            throw new RuntimeException("이미 친구 요청이 존재합니다.");
        }

        Friendship friendship = Friendship.builder()
                .sendUser(sender)
                .receiveUser(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);
        return "친구 요청이 전송되었습니다.";
    }

    // ✅ 2. 친구 요청 수락
    public String acceptFriendRequest(Long friendshipId, Long receiverId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("친구 요청을 찾을 수 없습니다."));

        // 수락자가 일치하는지 검증
        if (!friendship.getReceiveUser().getUserId().equals(receiverId)) {
            throw new RuntimeException("수락 권한이 없습니다.");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return "친구 요청을 수락했습니다.";
    }

    // ✅ 3. 친구 요청 거절
    public String rejectFriendRequest(Long friendshipId, Long receiverId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("친구 요청을 찾을 수 없습니다."));

        if (!friendship.getReceiveUser().getUserId().equals(receiverId)) {
            throw new RuntimeException("거절 권한이 없습니다.");
        }

        friendshipRepository.delete(friendship);   // ✅ 거절 시에도 삭제
        return "친구 요청을 거절했습니다.";
    }

    // ✅ 4. 친구 요청 취소 (보낸 사람이 취소)
    public String cancelFriendRequest(Long friendshipId, Long senderId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("친구 요청을 찾을 수 없습니다."));

        if (!friendship.getSendUser().getUserId().equals(senderId)) {
            throw new RuntimeException("취소 권한이 없습니다.");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new RuntimeException("이미 처리된 요청은 취소할 수 없습니다.");
        }

        friendshipRepository.delete(friendship);
        return "친구 요청을 취소했습니다.";
    }

    // ✅ 5. 내 친구 목록 (보낸/받은 관계 모두)
    @Transactional(readOnly = true)
    public List<FriendDTO> getFriendList(Long loginUserId) {
        UserEntity loginUser = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("로그인 유저 없음"));

        List<Friendship> sent = friendshipRepository.findBySendUserAndStatus(loginUser, FriendshipStatus.ACCEPTED);
        List<Friendship> received = friendshipRepository.findByReceiveUserAndStatus(loginUser, FriendshipStatus.ACCEPTED);

        return List.of(sent, received).stream()
                .flatMap(List::stream)
                .map(friendship -> FriendDTO.fromEntity(friendship, loginUserId))
                .collect(Collectors.toList());
    }


    // 두 유저가 친구인지 확인 (Accepted 상태인지)
    public boolean areFriends(UserEntity userA, UserEntity userB) {
        return friendshipRepository.findBySendUserAndReceiveUserAndStatus(userA, userB, FriendshipStatus.ACCEPTED).isPresent()
                || friendshipRepository.findByReceiveUserAndSendUserAndStatus(userA, userB, FriendshipStatus.ACCEPTED).isPresent();
    }

    public FriendSearchDTO searchFriend(UserEntity loginUser, String nickname) {

        // 1) 닉네임으로 사용자 검색
        UserEntity targetUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("해당 닉네임의 사용자를 찾을 수 없습니다."));

        // 2) 본인 검색 허용 뭔가  안되서 그냥 주석처리함. 시간남으면 해결함 ㅈㅅ
//        if (loginUser.getUserId().equals(targetUser.getUserId())) {
//            return new FriendSearchDTO(targetUser.getUserId(), targetUser.getNickname());
//        }

        // 3) 친구 여부는 단순 표시용으로만 사용 (검색 자체 막지 않음)
        boolean isFriend = areFriends(loginUser, targetUser);

        return new FriendSearchDTO(
                targetUser.getUserId(),
                targetUser.getNickname(),
                isFriend    // 필요하면 DTO에 친구 여부도 포함
        );
    }


    public List<FriendRecommendationDTO> getRecommendedFriends(Long loginUserId) {

        UserEntity loginUser = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("로그인 유저 없음"));

        // 1. STAR 상태의 유저 전체 조회
        List<UserEntity> stars = userRepository.findByStatus(UserStatus.STAR);

        // 2. 현재 유저 자신은 제외
        stars = stars.stream()
                .filter(u -> !u.getUserId().equals(loginUserId))
                .collect(Collectors.toList());

        // 3. 이미 친구인 사람 제외
        stars = stars.stream()
                .filter(u -> !areFriends(loginUser, u))
                .collect(Collectors.toList());

        // 4. 랜덤 셔플
        Collections.shuffle(stars);

        // 5. 최대 10명만 반환
        //친구인 경우 해당 유저를 배제하고 가져옴.
        return stars.stream()
                .limit(10)
                .map(u -> new FriendRecommendationDTO(
                        u.getUserId(),
                        u.getNickname(),
                        u.getProfileImgUrl(),   // ✅ 추가된 필드
                        u.getStatus()           // ✅ UserStatus 전달
                ))
                .collect(Collectors.toList());
    }

    public List<FriendDTO> getReceivedRequests(Long receiverId) {

        // 1) receiver(UserEntity) 찾기
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

        // 2) 받은 친구 요청 목록 가져오기 (상태: PENDING)
        List<Friendship> requests = friendshipRepository
                .findByReceiveUserAndStatus(receiver, FriendshipStatus.PENDING);

        // 3) DTO 반환
        return requests.stream()
                .map(f -> FriendDTO.fromEntity(f, receiverId))  // receiverId 전달
                .collect(Collectors.toList());
    }


}
