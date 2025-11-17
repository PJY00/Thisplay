package com.example.thisplay.global.jwt;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.Cookie;

@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    //요청의 nickname, password를 JSON에서 꺼낸어 로그인 정보가 맞는지 확인
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);

            String nickname = requestMap.get("nickname");
            String password = requestMap.get("password");

            // 닉네임 존재 여부 직접 체크
            if (!userRepository.existsByNickname(nickname)) {
                throw new UsernameNotFoundException("존재하지 않는 닉네임입니다.");
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(nickname, password);

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = customUserDetails.getUserEntity();

        Long userId = user.getUserId();
        String username = customUserDetails.getUsername();
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        // AccessToken, RefreshToken 발급
        String accessToken = jwtUtil.createJwt(userId, username, role, 10 * 60 * 1000L);  // 10분
        String refreshToken = jwtUtil.createJwt(userId, username, role, 24 * 60 * 60 * 1000L); // 24시간

        // DB에 RefreshToken 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

// AccessToken 쿠키 직접 설정
        response.addHeader("Set-Cookie",
                "accessToken=" + accessToken
                        + "; Path=/"
                        + "; HttpOnly"
                        + "; Secure"
                        + "; SameSite=None"
        );

// RefreshToken 쿠키 직접 설정
        response.addHeader("Set-Cookie",
                "refreshToken=" + refreshToken
                        + "; Path=/"
                        + "; HttpOnly"
                        + "; Secure"
                        + "; SameSite=None"
        );



        // JSON 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "로그인 성공");
        responseBody.put("data", data);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }


    //로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage;
        if (failed instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 닉네임입니다.";
        } else {
            errorMessage = "비밀번호가 틀렸습니다.";
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "로그인 실패");
        responseBody.put("error", errorMessage);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }
}