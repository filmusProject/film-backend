package com.filmus.backend.token.service;

import com.filmus.backend.token.entity.RefreshToken;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.token.repository.RefreshTokenRepository;
import com.filmus.backend.user.repository.UserRepository;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 리프레시 토큰 저장 (기존 토큰이 있으면 업데이트, 없으면 새로 생성)
    public void saveRefreshToken(User user, String newToken) {
        Optional<RefreshToken> existing = refreshTokenRepository.findByUser(user);

        if (existing.isPresent()) {
            existing.get().updateToken(newToken);
        } else {
            refreshTokenRepository.save(new RefreshToken(user, newToken));
        }
    }

//    // 리프레시 토큰 조회
//    public String getRefreshToken(User user) {
//        return refreshTokenRepository.findByUser(user)
//                .map(RefreshToken::getToken)
//                .orElse(null);
//    }

    // 리프레시 토큰 삭제 (로그아웃 시 사용)
    @Transactional
    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

    public String reissueAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getSubject(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        RefreshToken stored = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다."));

        if (!stored.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        return jwtTokenProvider.createAccessToken(user);
    }
}
