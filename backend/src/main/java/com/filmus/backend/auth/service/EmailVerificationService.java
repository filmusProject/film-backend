package com.filmus.backend.auth.service;

import com.filmus.backend.auth.entity.EmailVerificationToken;
import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.auth.repository.EmailVerificationTokenRepository;
import com.filmus.backend.user.repository.UserRepository;
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

    //생성 후 저장
    public String generateToken(User user){
        // 기존 토큰이 있다면 삭제
        tokenRepository.deleteByUser(user);
        //토큰 새로 생성
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(user);
        //토큰 저장
        tokenRepository.save(emailVerificationToken);

        // 생성된 토큰 반환
        return emailVerificationToken.getToken();

    }

    // 이메일 인증 처리
    @Transactional
    public boolean verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.isExpired()) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = verificationToken.getUser();
        user.verifyEmail(); // 사용자 엔티티에 이메일 인증 상태 변경 메서드 필요
        tokenRepository.delete(verificationToken);
        return true;
    }
}
