package com.filmus.backend.auth.entity;

import com.filmus.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 이메일 인증을 위한 토큰 정보를 저장하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor
public class EmailVerificationToken {

    @Id
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 생성자에 @Builder를 적용해 외부에서 빌더 방식으로 객체 생성 가능하게 합니다.
     */
    @Builder
    public EmailVerificationToken(User user) {
        this.token = UUID.randomUUID().toString();
        this.user = user;
        this.expiresAt = LocalDateTime.now().plusMinutes(30);
    }

    /**
     * DB 저장 시점에 자동으로 생성 시간을 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 현재 시각 기준으로 토큰이 만료되었는지 확인합니다.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}