package com.filmus.backend.auth.service;

import com.filmus.backend.auth.entity.EmailVerificationToken;
import com.filmus.backend.auth.entity.User;
import com.filmus.backend.auth.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이메일 인증 토큰 검증 및 사용자 인증 상태를 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;

    // 이메일 인증 처리
    @Transactional
    public boolean verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null || verificationToken.isExpired()) {
            return false;
        }

        User user = verificationToken.getUser();
        user.verifyEmail(); // 사용자 엔티티에 이메일 인증 상태 변경 메서드 필요
        tokenRepository.delete(verificationToken);
        return true;
    }
}
