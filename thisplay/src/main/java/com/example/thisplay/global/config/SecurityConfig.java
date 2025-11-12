//
////Spring Security 설정 담당 파일
//package com.example.thisplay.global.config;
//
//import com.example.thisplay.global.jwt.JWTFilter;
//import com.example.thisplay.global.jwt.JWTUtil;
//import com.example.thisplay.global.jwt.LoginFilter;
//import com.example.thisplay.common.Auth.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity // 보안 설정 클래스
//@AllArgsConstructor
//@EnableMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    private final AuthenticationConfiguration authenticationConfiguration;
//    private final JWTUtil jwtUtil;
//    private final UserRepository userRepository; // 추가
//
//    //사용자 인증 처리
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    //비밀번호 암호화
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //보안 규칙 정의
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//                .logout(logout -> logout.disable()); // 기본 로그아웃 비활성화
//
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/", "/join", "/logout", "/api/main/**","/api/movies/show/**","/oauth2/**", "/login/oauth2/**","/api/likes/**", "/login_join/**").permitAll() //인증 없이 접근 가능
//                        .requestMatchers("/api/users/*/profile", "api/reviews/**", "api/likes/**").authenticated()
//                        .requestMatchers(HttpMethod.GET, "/api/reviews/**", "api/likes/**").permitAll()
//                        .anyRequest().authenticated());
//
//        // LoginFilter 등록
//        http
//                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, userRepository),
//                        UsernamePasswordAuthenticationFilter.class);
//
//        // JWTFilter 등록
//        http
//                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
//
//
//        http
//                .oauth2Login(oauth -> oauth
//                        .successHandler((request, response, authentication) -> {
//                            response.sendRedirect("/loginSuccess");
//                        })
//                        .failureHandler((request, response, exception) -> {
//                            response.sendRedirect("/loginFailure");
//                        })
//                );
//
//        http
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
//
//        return http.build();
//    }
//}

package com.example.thisplay.global.config;

import com.example.thisplay.global.jwt.JWTFilter;
import com.example.thisplay.global.jwt.JWTUtil;
import com.example.thisplay.global.jwt.LoginFilter;
import com.example.thisplay.common.Auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // AuthenticationManager 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 시큐리티 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil, userRepository);
        loginFilter.setFilterProcessesUrl("/api/login");

        // 기본 보안 기능 해제
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable());

        // 경로별 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/join", "/login_join/**",
                                "/oauth2/**", "/login/oauth2/**",
                                "/api/main/**", "/api/movies/show/**",
                                "/api/likes/**", "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**", "/api/likes/**").permitAll()
                        .anyRequest().authenticated()
                );

        // 폼 로그인 설정
        http
                .formLogin(form -> form
                        .loginPage("/login")               // GET /login → Controller에서 login.html 렌더링
                        .loginProcessingUrl("/login")      // POST /login → 로그인 처리
                        .defaultSuccessUrl("/", true)      // 로그인 성공 시 이동
                        .permitAll()
                )

                // OAuth2 로그인 (구글 로그인)
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")               // 같은 페이지에서 구글 로그인 버튼 제공
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/loginSuccess");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/loginFailure");
                        })
                );

        // JWT / Login 필터 등록
//        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, userRepository);
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        // 세션 정책 (JWT 기반이므로 비상시만 생성)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

        return http.build();
    }
}