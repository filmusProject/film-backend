package com.filmus.backend.token.service;

import com.filmus.backend.user.entity.User;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 15; // 15분
    private static final long REFRESH_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(User user) {
        return createToken(user.getEmail(), ACCESS_TOKEN_EXPIRE);
    }

    public String createRefreshToken(User user) {
        return createToken(user.getEmail(), REFRESH_TOKEN_EXPIRE);
    }

    private String createToken(String subject, long expireTimeMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireTimeMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // ← subject에 userId 넣었다면 이거 맞음
    }
}