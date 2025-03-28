package com.filmus.backend.auth.service;

import com.filmus.backend.auth.dto.SignupRequestDto;
import com.filmus.backend.auth.entity.User;
import com.filmus.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 사용자 인증 및 회원 관련 핵심 로직을 담당하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository; // 사용자 엔티티에 접근하는 JPA 리포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 의존성
    private final EmailService emailService; // 이메일 전송 로직

    /**
     * 이메일 인증 확인 요청 처리
     * @param token 이메일 인증 토큰
     * @param emailVerificationService 이메일 인증 처리 서비스
     * @return 인증 성공 여부
     */
    @Transactional
    public boolean verifyEmail(String token, EmailVerificationService emailVerificationService) {
        return emailVerificationService.verifyEmailToken(token);
    }

    /**
     * 사용자 ID로 사용자 조회
     * @param userId 사용자 ID
     * @return User 객체
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    /**
     * 로그인 시 사용자 유효성 검증 (비밀번호 해시 비교)
     * @param username 아이디
     * @param password 입력한 비밀번호
     * @return 유효한 사용자면 User, 아니면 null
     */
    public User validateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(null);
    }

    /**
     * 회원가입 처리
     * @param request 클라이언트로부터 전달받은 회원가입 요청 DTO
     */
    @Transactional
    public void signup(SignupRequestDto request) {
        // username/email 중복 여부 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화 후 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .isVerified(false)  // ★ 명시적으로 false 설정
                .build();

        // 1. DB에 사용자 저장 → ID가 생성되어 외래 키 참조 가능해짐
        User savedUser = userRepository.save(newUser);

        // 2. 저장된 사용자 기준으로 이메일 인증 토큰 전송
        emailService.sendVerificationEmail(savedUser);
    }
}
