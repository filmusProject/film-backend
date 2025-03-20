package com.filmus.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명에 사용할 키 (HS256 알고리즘을 위한 Secret Key 생성)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰의 유효 시간 (1시간)
    private final long validityInMilliseconds = 3600000;

    // JWT 토큰을 생성하는 메서드
    public String createToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username) // 사용자 정보 설정
                .setIssuedAt(now) // 생성 시간
                .setExpiration(validity) // 만료 시간 설정
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 키 및 알고리즘 설정
                .compact(); // 최종적으로 JWT 문자열 생성
    }

    // JWT 토큰을 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 검증
            return true; // 검증 성공 시 true 반환
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 토큰이 유효하지 않거나 만료되었으면 false 반환
        }
    }

    // JWT 토큰에서 사용자 이름(Username)을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody() // Payload 가져오기
                .getSubject(); // subject 값을 username으로 사용
    }
}
