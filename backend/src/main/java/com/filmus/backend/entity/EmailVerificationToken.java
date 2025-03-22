package com.filmus.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity  // JPA 엔티티로 선언
@Getter  // 모든 필드에 대한 getter 자동 생성
@NoArgsConstructor  // 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 받는 전체 생성자 자동 생성
@Builder  // 빌더 패턴으로 객체 생성 가능
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키 (자동 증가)

    @Column(nullable = false, unique = true)
    private String token;  // 인증용 토큰 문자열

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;  // 이 토큰이 연결된 사용자 정보

    @Column(nullable = false)
    private LocalDateTime expirationDate;  // 토큰 만료 시간

    private LocalDateTime createdAt = LocalDateTime.now();  // 생성 시각 (기본값 현재)
}
