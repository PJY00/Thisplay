
//Spring Security 설정 담당 파일
package com.example.thisplay.global.config;

import com.example.thisplay.global.jwt.JWTFilter;
import com.example.thisplay.global.jwt.JWTUtil;
import com.example.thisplay.global.jwt.LoginFilter;
import com.example.thisplay.common.Auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable()); // 기본 로그아웃 비활성화

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/", "/join", "/logout", "/api/main/**","/api/movies/show/**","/oauth2/**", "/login/oauth2/**").permitAll() //인증 없이 접근 가능
                        .requestMatchers("/api/users/*/profile").authenticated()
                        .anyRequest().authenticated());

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
                            response.sendRedirect("/loginSuccess");
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
}
