package com.example.thisplay.common.friend.controller;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.friend.dto.FriendDTO;
import com.example.thisplay.common.friend.dto.FriendRequestDTO;
import com.example.thisplay.common.friend.dto.FriendSearchDTO;
import com.example.thisplay.common.friend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/friends")
@RequiredArgsConstructor
public class FriendshipController {
    //로그인해서 Authorization에서 인증 받으면 사용자는 자동으로 입력이 됩니다. 헤더에 Authorization꼭 주세요
    private final FriendshipService friendshipService;

    // 1️⃣ 친구 요청 보내기
    @PostMapping("/request")
    public ResponseEntity<String> sendFriendRequest(
            @RequestBody FriendRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long senderId = userDetails.getUserId(); // ✅ 로그인한 유저 ID 자동 가져오기
        return ResponseEntity.ok(
                friendshipService.sendFriendRequest(senderId, requestDTO.getReceiverNickname())
        );
    }

    // 2️⃣ 친구 요청 수락
    //friednshipid: DB에 friendshipId가 있는데 그거 확인해서 사용하시면 됩니다.
    @PostMapping("/{friendshipId}/accept")
    public ResponseEntity<String> acceptFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long receiverId = userDetails.getUserId(); // ✅ 자동 처리
        return ResponseEntity.ok(
                friendshipService.acceptFriendRequest(friendshipId, receiverId)
        );
    }

    // 3️⃣ 친구 요청 거절
    @DeleteMapping("/{friendshipId}/reject")
    public ResponseEntity<String> rejectFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long receiverId = userDetails.getUserId();
        return ResponseEntity.ok(
                friendshipService.rejectFriendRequest(friendshipId, receiverId)
        );
    }

    // 4️⃣ 친구 요청 취소
    @DeleteMapping("/{friendshipId}/cancel")
    public ResponseEntity<String> cancelFriendRequest(
            @PathVariable Long friendshipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long senderId = userDetails.getUserId();
        return ResponseEntity.ok(
                friendshipService.cancelFriendRequest(friendshipId, senderId)
        );
    }

    // 5️⃣ 친구 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<FriendDTO>> getFriendList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(friendshipService.getFriendList(userId));
    }

    // 6친구 찾기
    @PostMapping("/search")
    public ResponseEntity<FriendSearchDTO> searchFriend(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        return ResponseEntity.ok(friendshipService.searchUserByNickname(nickname));
    }
}
