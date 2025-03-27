package com.filmus.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 정보를 저장하는 User 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 보호
@AllArgsConstructor
@Builder
@Table(name = "users") // 명시적으로 테이블명 지정
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자 (자동 증가)

    @Column(nullable = false, unique = true)
    private String username; // 사용자 아이디 (중복 불가)

    @Setter(AccessLevel.PRIVATE) // 외부 노출 제한
    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일 주소 (중복 불가)

    @Column(nullable = false)
    private String nickname; // 사용자 닉네임

    @Column
    private String gender; // 성별 (선택값)

    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일 (선택값)

    @Setter // 이메일 인증 필드만 setter 허용
    @Column(name = "is_verified")
    private boolean isVerified = false; // 이메일 인증 여부 (기본 false)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 시각

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 시각

    // 이메일 인증 완료 처리 메서드
    public void verifyEmail() {
        this.isVerified = true;
    }

    // 비밀번호 변경 메서드 (임시 비밀번호 발급 시 사용)
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
