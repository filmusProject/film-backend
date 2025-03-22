package com.filmus.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (H2 콘솔 사용 가능하도록 설정)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/protected/**").authenticated()  // 인증이 필요한 경로
                        .anyRequest().permitAll()  // 그 외 모든 요청은 허용
                )
                .headers(headers -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)) // 최신 방식 적용
                )
                .formLogin(form -> form.disable())  // 기본 로그인 폼 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());  // HTTP Basic 인증 비활성화

        return http.build();
    }
}
