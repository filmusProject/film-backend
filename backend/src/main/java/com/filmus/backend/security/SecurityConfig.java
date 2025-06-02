package com.filmus.backend.security;

import com.filmus.backend.oauth.handler.OAuth2SuccessHandler;
import com.filmus.backend.oauth.service.CustomOAuth2UserService;
import com.filmus.backend.token.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// 주석을 추가할 것
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;                       // JWT 생성/검증을 위한 유틸
    private final JwtAuthenticationFilter jwtAuthenticationFilter;         // JWT 인증 필터
    private final CustomOAuth2UserService customOAuth2UserService;         // 카카오 사용자 정보 처리 서비스
    private final OAuth2SuccessHandler oAuth2SuccessHandler;               // 로그인 성공 후 JWT 발급 및 리다이렉트

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/protected/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 👈 관리자 전용 경로 추가
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                )
                .oauth2Login(oauth2 -> oauth2         // ✅ 소셜 로그인 설정 시작
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))              // 사용자 정보 처리
                        .successHandler(oAuth2SuccessHandler)                       // 로그인 성공 후 토큰 발급 및 리다이렉트
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 등록
                .formLogin(form -> form.disable())  // 기본 로그인 폼 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());  // HTTP Basic 인증 비활성화

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",           // ✅ 로컬 프론트엔드 주소
                "https://filmus.o-r.kr"             // ✅ 배포된 프론트 주소
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    /**
//     * 비밀번호 암호화를 위한 Bcrypt 설정
//     */
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
