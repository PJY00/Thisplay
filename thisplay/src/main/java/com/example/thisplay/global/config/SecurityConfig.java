
//Spring Security 설정 담당 파일
package com.example.thisplay.global.config;

import com.example.thisplay.common.Auth.Entity.UserEntity;
import com.example.thisplay.common.Auth.Entity.UserStatus;
import com.example.thisplay.global.jwt.JWTFilter;
import com.example.thisplay.global.jwt.JWTUtil;
import com.example.thisplay.global.jwt.LoginFilter;
import com.example.thisplay.common.Auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;

@Configuration
@EnableWebSecurity // 보안 설정 클래스
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository; // 추가

    //사용자 인증 처리
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //보안 규칙 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable()); // 기본 로그아웃 비활성화

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/login","/", "/join", "/logout", "/api/main/**","/api/movies/show/**","/oauth2/**", "/login/oauth2/**", "/api/**", "/images/**").permitAll() //인증 없이 접근 가능
                        .anyRequest().authenticated());

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\": \"UNAUTHORIZED\"}");
                })
        );

        // LoginFilter 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, userRepository),
                        UsernamePasswordAuthenticationFilter.class);

        // JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);


        http
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {

                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            //  구글 사용자 정보 가져오기
                            String googleId = oAuth2User.getAttribute("sub");
                            String email = oAuth2User.getAttribute("email");
                            String name = oAuth2User.getAttribute("name");

                            //  DB에서 유저 찾기, 없으면 자동 생성
                            UserEntity user = userRepository.findByGoogleId(googleId)
                                    .orElseGet(() -> {
                                        UserEntity newUser = UserEntity.builder()
                                                .googleId(googleId)
                                                .email(email)
                                                .nickname(name)
                                                .role("ROLE_USER")
                                                .status(UserStatus.NORMAL)
                                                .folders(new ArrayList<>())              // 추가
                                                .friendshipList(new ArrayList<>())       //추가
                                                .receivedFriendships(new ArrayList<>())
                                                .build();
                                        return userRepository.save(newUser);
                                    });

                            //  JWT 생성
                            String accessToken = jwtUtil.createJwt(
                                    user.getUserId(),
                                    user.getNickname(),
                                    user.getRole(),
                                    10 * 60 * 1000L // 10분
                            );

                            String refreshToken = jwtUtil.createJwt(
                                    user.getUserId(),
                                    user.getNickname(),
                                    user.getRole(),
                                    24 * 60 * 60 * 1000L // 24시간
                            );

                            //  DB에 Refresh Token 저장
                            user.setRefreshToken(refreshToken);
                            userRepository.save(user);

                            //  쿠키 설정 (Access Token)
                            response.addHeader("Set-Cookie",
                                    "accessToken=" + accessToken
                                            + "; Path=/"
                                            + "; HttpOnly"
                                            + "; Secure"
                                            + "; SameSite=None"
                            );

                            //  쿠키 설정 (Refresh Token)
                            response.addHeader("Set-Cookie",
                                    "refreshToken=" + refreshToken
                                            + "; Path=/"
                                            + "; HttpOnly"
                                            + "; Secure"
                                            + "; SameSite=None"
                            );

                            // 로그인 성공 후 redirect
                             response.sendRedirect("/loginSuccess");
                            // response.sendRedirect("http://127.0.0.1:5500/thisplay/src/main/resources/templates/mainpage/mainpage.html");

                        })

                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/loginFailure");
                        })
                );


        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        //addAllowedOrigin 대신 addAllowedOriginPattern 사용
        config.addAllowedOriginPattern("http://localhost:*");
        config.addAllowedOriginPattern("http://127.0.0.1:*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // 쿠키 허용
        config.setAllowCredentials(true);

        // Set-Cookie 헤더 노출
        config.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
