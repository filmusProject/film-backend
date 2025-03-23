package com.filmus.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity  // JPA 테이블과 매핑될 엔티티임을 나타냄
@Getter  // 모든 필드의 Getter 메서드를 자동으로 생성함
@Setter  // 모든 필드의 Setter 메서드를 자동으로 생성함
@NoArgsConstructor  // 기본 생성자 자동 생성 (파라미터 없는 생성자)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
@Builder  // 빌더 패턴 사용 가능: User.builder().username("test").build() 형태로 객체 생성 가능
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키 (자동 증가)

    @Column(nullable = false, unique = true)
    private String username;  // 사용자 ID 또는 이메일

    @Column(nullable = false)
    private String password;  // 비밀번호 (BCrypt 등으로 암호화됨)

    @Column(nullable = false, unique = true)
    private String email;  // 사용자 이메일 (중복 방지)

    @Column(nullable = false, unique = true)
    private String nickname;  // 닉네임 (중복 방지)

    private String gender;  // 선택 항목: 성별 (M / F 등)

    private LocalDate birthDate;  // 선택 항목: 생년월일

    @Column(nullable = false)
    private boolean emailVerified = false;  // 이메일 인증 여부 (기본값: false)
}
