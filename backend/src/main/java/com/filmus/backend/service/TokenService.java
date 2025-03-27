package com.filmus.backend.service;

import com.filmus.backend.entity.RefreshToken;
import com.filmus.backend.entity.User;
import com.filmus.backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 리프레시 토큰을 저장하고 관리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰 저장 (기존 토큰이 있으면 업데이트, 없으면 새로 생성)
    public void saveRefreshToken(User user, String newToken) {
        Optional<RefreshToken> existing = refreshTokenRepository.findByUser(user);

        if (existing.isPresent()) {
            existing.get().updateToken(newToken);
        } else {
            refreshTokenRepository.save(new RefreshToken(user, newToken));
        }
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(User user) {
        return refreshTokenRepository.findByUser(user)
                .map(RefreshToken::getToken)
                .orElse(null);
    }

    // 리프레시 토큰 삭제 (로그아웃 시 사용)
    @Transactional
    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
