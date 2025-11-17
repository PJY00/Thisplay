//검증된 사용자 정보를 등록
package com.example.thisplay.global.jwt;

import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
import com.example.thisplay.common.Auth.Entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    //생성자 방식으로 JWTUTil을 주입받음
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // /api/movies/**, 경로는 JWT 검증 건너뜀
        if (path.startsWith("/api/main")||path.startsWith("/api/movies/show")||path.startsWith("/login")||path.startsWith("/join")) {
            filterChain.doFilter(request, response);
            return;
        }
        //---여기까지 변경 사항

        //request에서 Authorization 헤더를 찾음
        String token = null;

        // 1. Authorization 헤더에서 토큰 추출
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.split(" ")[1];
        }

        // 2. Authorization 헤더가 없으면 쿠키에서 accessToken 추출
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // 토큰이 여전히 없으면 그냥 다음 필터 진행
        if (token == null) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 username, role 꺼내기 +UserId추가
        String nickname = jwtUtil.getNickname(token);
        String role = jwtUtil.getRole(token);
        Long userId=jwtUtil.getUserId(token);

        UserEntity userEntity = UserEntity.builder()
                .nickname(nickname)
                .password("temppassword") // 임시 패스워드
                .role(role)
                .userId(userId)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

//선영이가 수정한 버전
//package com.example.thisplay.global.jwt;
//
//import com.example.thisplay.common.Auth.DTO.CustomUserDetails;
//import com.example.thisplay.common.Auth.Entity.UserEntity;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class JWTFilter extends OncePerRequestFilter {
//    private final JWTUtil jwtUtil;
//
//    //생성자 방식으로 JWTUTil을 주입받음
//    public JWTFilter(JWTUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        // /api/movies/**, 경로는 JWT 검증 건너뜀
//        if (path.startsWith("/api/main")||path.startsWith("/api/movies/show")||path.startsWith("/login")||path.startsWith("/join")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//        //---여기까지 변경 사항
//
//        //request에서 Authorization 헤더를 찾음
//        String token = null;
//
//        // 1. Authorization 헤더에서 토큰 추출
//        String authorization = request.getHeader("Authorization");
//        if (authorization != null && authorization.startsWith("Bearer ")) {
//            token = authorization.split(" ")[1];
//        }
//
//        // 2. Authorization 헤더가 없으면 쿠키에서 accessToken 추출
//        if (token == null) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if ("accessToken".equals(cookie.getName())) {
//                        token = cookie.getValue();
//                        break;
//                    }
//                }
//            }
//        }
//
//        // 토큰이 여전히 없으면 그냥 다음 필터 진행
//        if (token == null) {
//            System.out.println("token null");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // 토큰 만료 여부 확인
//        if (jwtUtil.isExpired(token)) {
//            System.out.println("token expired");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // 토큰에서 username, role 꺼내기 +UserId추가
//        String nickname = jwtUtil.getNickname(token);
//        String role = jwtUtil.getRole(token);
//        Long userId=jwtUtil.getUserId(token);
//
//        UserEntity userEntity = UserEntity.builder()
//                .nickname(nickname)
//                .password("temppassword") // 임시 패스워드
//                .role(role)
//                .userId(userId)
//                .build();
//
//        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
//
//        Authentication authToken =
//                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
//    }
//}