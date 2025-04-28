package com.filmus.backend.auth.service;

import com.filmus.backend.auth.dto.LoginRequestDto;
import com.filmus.backend.auth.dto.SignupRequestDto;
import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.token.service.JwtTokenProvider;
import com.filmus.backend.common.util.CookieUtil;
import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final TokenService tokenService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;

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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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
     * 사용자 로그인 처리 메서드.
     * <p>
     * 사용자가 입력한 이메일과 비밀번호를 기반으로 인증을 수행하고,
     * 이메일 인증이 완료된 유저인 경우 JWT Access Token과 Refresh Token을 발급합니다.
     * Access Token은 반환하고, Refresh Token은 HttpOnly 쿠키로 클라이언트에 전송됩니다.
     * <p>
     * 또한 Refresh Token은 서버(DB 또는 Redis 등)에 저장하여 추후 재발급에 활용합니다.
     *
     * @param request 로그인 요청 DTO (이메일, 비밀번호)
     * @param response HttpServletResponse – 쿠키 설정을 위해 사용
     * @return 발급된 Access Token (JWT 문자열)
     *
     * @throws BadCredentialsException 이메일 또는 비밀번호가 올바르지 않은 경우
     * @throws AuthenticationCredentialsNotFoundException 이메일 인증이 완료되지 않은 경우
     */

    public String login(LoginRequestDto request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!user.isVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // DB or Redis에 refreshToken 저장
        tokenService.saveRefreshToken(user, refreshToken);

        // 쿠키에 refreshToken 설정
        Cookie cookie = cookieUtil.createSecureHttpOnlyCookie("refreshToken", refreshToken, 7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return accessToken;
    }

    /**
     * 회원가입 처리
     * @param request 클라이언트로부터 전달받은 회원가입 요청 DTO
     */
    @Transactional
    public void signup(SignupRequestDto request) {
        // username/email 중복 여부 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        // 요청에서 role이 안 왔으면 기본 ROLE_USER
        String role = (request.getRole() != null) ? request.getRole() : "ROLE_USER";

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
                .role(role)
                .build();

        // 1. DB에 사용자 저장 → ID가 생성되어 외래 키 참조 가능해짐
        User savedUser = userRepository.save(newUser);

        // 2. 저장된 사용자 기준으로 이메일 인증 토큰 전송
        emailService.sendVerificationEmail(savedUser);
    }

    /**
     * 로그아웃
     */
    public void logout(User user, HttpServletResponse response) {
        // 서버에서 refreshToken 제거
        tokenService.deleteRefreshToken(user);

        // 클라이언트 쿠키 삭제
        Cookie expiredCookie = cookieUtil.deleteCookie("refreshToken");
        response.addCookie(expiredCookie);
    }

    @Transactional
    public void adminSignup(SignupRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .isVerified(true) // 관리자라면 바로 인증 처리할 수도 있음
                .role("ROLE_ADMIN") // 👈 무조건 ROLE_ADMIN
                .build();

        userRepository.save(newUser);
    }
}
