package com.filmus.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 설정 클래스입니다.
 * - 이 설정을 통해 전역에서 BCryptPasswordEncoder를 주입받아 사용할 수 있습니다.
 * - 순환참조 문제를 방지하고, 보안 설정을 모듈화할 수 있습니다.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * BCrypt 해시 알고리즘 기반 비밀번호 인코더를 Bean으로 등록합니다.
     * - 보안적으로 안전한 해시 방식이며, 매 요청마다 다른 salt를 사용합니다.
     * - Spring Security에서 기본적으로 사용하는 암호화 방식입니다.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

