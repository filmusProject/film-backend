package com.filmus.backend.auth.entity;

import jakarta.persistence.*;

/**
 * 사용자와 연결된 리프레시 토큰을 저장하는 엔티티 클래스입니다.
 * - 리프레시 토큰은 DB에 저장하여 유효성 검증 및 로그아웃 처리를 할 수 있도록 합니다.
 */
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키 (자동 증가)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 리프레시 토큰과 연결된 사용자

    @Column(nullable = false, length = 500)
    private String token; // 실제 리프레시 토큰 문자열

    protected RefreshToken() {}

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void updateToken(String newToken) {
        this.token = newToken;
    }
}