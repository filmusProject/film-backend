package com.filmus.backend.auth.repository;

import com.filmus.backend.auth.entity.EmailVerificationToken;
import com.filmus.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 이메일 인증 토큰을 조회 및 삭제할 수 있는 JPA 리포지토리입니다.
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    // 토큰 문자열로 인증 토큰 엔티티 조회
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser(User user);
    // 사용자가 이미 인증 토큰을 발급받은 경우 제거
    void deleteByToken(String token);
}
