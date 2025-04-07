package com.filmus.backend.token.service;

import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.user.entity.User;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 15;       // 15분
    private static final long REFRESH_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    protected void init() {
        // secretKey를 Base64로 인코딩 (토큰 파싱용)
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * AccessToken 생성
     * Subject 부분에 User의 numeric ID를 넣는다.
     */
    public String createAccessToken(User user) {
        // PK(Long) → String으로 변환해서 subject로 사용
        return createToken(String.valueOf(user.getId()), ACCESS_TOKEN_EXPIRE);
    }

    /**
     * RefreshToken 생성
     * Subject에 User ID를 동일하게 넣고, 유효기간만 다름.
     */
    public String createRefreshToken(User user) {
        return createToken(String.valueOf(user.getId()), REFRESH_TOKEN_EXPIRE);
    }

    /**
     * JWT 토큰 생성 메서드
     * Subject = User의 numeric ID
     * expireTimeMillis 만큼 유효기간
     */
    private String createToken(String subject, long expireTimeMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTimeMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 토큰에서 Subject(=User ID)를 추출
     */
    public String getSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_JWT);
        }
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * userId(Subject) 반환용.
     * 편의 메서드: 내부적으로 getSubject() 사용
     */
    public String getUserId(String token) {
        return getSubject(token);
    }
}