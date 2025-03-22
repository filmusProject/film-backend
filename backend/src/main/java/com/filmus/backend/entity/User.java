package com.filmus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

// JPA Entity로 선언 (DB 테이블과 매핑됨)
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    // 기본 키 (자동 증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 아이디 (중복 불가, not null)
    @Column(nullable = false, unique = true)
    private String username;

    // 암호화된 비밀번호
    @Column(nullable = false)
    private String password;

    // 이메일 주소 (중복 불가, not null)
    @Column(nullable = false, unique = true)
    private String email;

    // 사용자 닉네임
    @Column(nullable = false)
    private String nickname;

    // 성별 (선택 입력)
    @Column
    private String gender;

    // 생년월일 (선택 입력)
    @Column
    private LocalDate birthDate;

    // 이메일 인증 여부 (기본값: false)
    @Column(nullable = false)
    private boolean emailVerified = false;

    // 가입일시 (기본값: 현재 시간)
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 생성자 (builder 패턴을 사용하기 위해 필드별로 선언)
    @Builder
    public User(String username, String password, String email, String nickname, String gender, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    // 이메일 인증 완료 처리 메서드
    public void verifyEmail() {
        this.emailVerified = true;
    }
}
