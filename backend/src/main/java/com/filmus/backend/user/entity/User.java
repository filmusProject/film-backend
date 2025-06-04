package com.filmus.backend.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(nullable = false, unique = true) // 카카오 소셜 로그인에서 이메일을 받아올수없어서 nullable로 바꿈
    private String username; // 사용자 아이디 (중복 불가)

    @Setter(AccessLevel.PRIVATE) // 외부 노출 제한
    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false, unique = true) // 카카오 소셜 로그인에서 이메일을 받아올수없어서 nullable로 바꿈
    private String email; // 이메일 주소 (중복 불가)

    @Setter
    @Column(nullable = false)
    private String nickname; // 사용자 닉네임

    @Setter
    @Column
    private String gender; // 성별 (선택값)

    @Setter
    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일 (선택값)

    @Setter // 이메일 인증 필드만 setter 허용
    @Column(name = "is_verified")
    private boolean isVerified = false; // 이메일 인증 여부 (기본 false)

    @Setter
    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER"; // 기본 ROLE_USER

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 시각

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 시각

    // ✅ 소셜 로그인 관련 필드 추가
    @Column(nullable = false)
    private String provider;   // ex: "KAKAO"

    @Column(nullable = false)
    private String providerId; // ex: "12345678" (카카오 고유 ID)

    // 이메일 인증 완료 처리 메서드
    public void verifyEmail() {
        this.isVerified = true;
    }

    // 비밀번호 변경 메서드 (임시 비밀번호 발급 시 사용)
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // ✅ 소셜 회원 생성자 (카카오 자동 회원가입 시 사용, 일반 회원과 분리함)
    // User.java

//    public static User createSocialUser(String email, String nickname, String provider, String providerId, String encodedPassword) {
//        return User.builder()
//                .email(email)
//                .username(email) // 소셜 로그인 시 이메일이 없다면 null이어도 무방
//                .nickname(nickname)
//                .provider(provider)
//                .providerId(providerId)
//                .role("ROLE_USER")
//                .isVerified(true) // 이메일 인증 생략
//                .password(encodedPassword) // 더미 비밀번호
//                .build();
//    }
// 기존 방식 유지
public static User createSocialUser(String email, String nickname, String provider, String providerId, String encodedPassword) {
    if (encodedPassword == null) {
        throw new IllegalArgumentException("암호화된 비밀번호는 null일 수 없습니다.");
    }

    return User.builder()
            .email(email)
            .username(email != null ? email : "kakao_" + providerId) // username null 방지
            .nickname(nickname != null ? nickname : "소셜사용자")
            .provider(provider)
            .providerId(providerId)
            .password(encodedPassword) // 이미 암호화된 값만 받음
            .role("ROLE_USER")
            .isVerified(true)
            .build();
}



}
