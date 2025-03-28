package com.filmus.backend.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰을 생성하고 검증하는 유틸리티 클래스입니다.
 * - Access 토큰과 Refresh 토큰을 각각 생성
 * - 토큰에서 사용자 정보를 추출
 * - 토큰 유효성 검증 수행
 */
@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // JWT 서명에 사용할 키 (HS256 알고리즘용)
    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 액세스 토큰 유효시간: 30분 (밀리초 단위)
    private final long accessTokenValidity = 1000 * 60 * 30;

    // 리프레시 토큰 유효시간: 7일
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7;

    /**
     * 액세스 토큰을 생성합니다.
     * @param userId 사용자 ID 또는 username
     * @return JWT 액세스 토큰
     */
    public String createAccessToken(String userId) {
        return createToken(userId, accessTokenValidity);
    }

    /**
     * 리프레시 토큰을 생성합니다.
     * @param userId 사용자 ID 또는 username
     * @return JWT 리프레시 토큰
     */
    public String createRefreshToken(String userId) {
        return createToken(userId, refreshTokenValidity);
    }

    /**
     * JWT 토큰을 생성하는 내부 공통 메서드입니다.
     * @param userId subject로 사용할 사용자 식별자
     * @param validity 유효시간(ms)
     * @return JWT 문자열
     */
    private String createToken(String userId, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(userId) // 사용자 정보 설정
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiry) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 키와 알고리즘
                .compact(); // 최종 JWT 문자열 생성
    }

    /**
     * 전달된 JWT 토큰의 유효성을 검사합니다.
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("토큰 검증 시작: {}", token);
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱
            logger.debug("토큰 검증 성공");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID(또는 username)를 추출합니다.
     * @param token JWT 토큰
     * @return 사용자 ID (subject 필드)
     */
    public String getUserId(String token) {
        try {
            logger.debug("토큰에서 사용자 ID 추출 시작: {}", token);
            String userId = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // subject 필드에 저장된 userId 반환
            logger.debug("사용자 ID 추출 성공: {}", userId);
            return userId;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("사용자 ID 추출 실패: {}", e.getMessage());
            throw e;
        }
    }
}
