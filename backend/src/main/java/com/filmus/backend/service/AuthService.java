package com.filmus.backend.service;

import com.filmus.backend.dto.ChangePasswordRequestDto;
import com.filmus.backend.dto.ResetPasswordRequestDto;
import com.filmus.backend.dto.SignupRequestDto;
import com.filmus.backend.entity.EmailVerificationToken;
import com.filmus.backend.entity.User;
import com.filmus.backend.repository.EmailVerificationTokenRepository;
import com.filmus.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor  // final 필드 자동 생성자 주입
@Service
public class AuthService {

    private final UserRepository userRepository;  // 사용자 레포지토리
    private final BCryptPasswordEncoder passwordEncoder;  // 비밀번호 암호화
    private final EmailVerificationTokenRepository tokenRepository;  // 이메일 인증 토큰 레포지토리
    private final EmailService emailService;  // 이메일 발송 서비스

    // 로그인 시 이메일 인증 여부도 검사
    public boolean validateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(User::isEmailVerified)  // 이메일 인증된 사용자만 로그인 가능
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    // 회원가입 처리
    public void signup(SignupRequestDto request) {
        // 1. 아이디 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 생년월일 파싱 (선택)
        LocalDate birthDate = null;
        if (request.getBirthDate() != null && !request.getBirthDate().isBlank()) {
            birthDate = LocalDate.parse(request.getBirthDate());
        }

        // 5. User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthDate(birthDate)
                .build();

        userRepository.save(user);  // DB에 저장

        // 6. 이메일 인증 메일 전송
        emailService.sendVerificationEmail(user);  // 인증 메일 발송
    }

    // 이메일 인증 처리 메서드
    @Transactional
    public boolean verifyEmail(String token) {
        Optional<EmailVerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return false;
        }

        EmailVerificationToken emailToken = optionalToken.get();

        if (emailToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = emailToken.getUser();
        user.setEmailVerified(true);  // 인증 완료 처리
        userRepository.save(user);

        tokenRepository.delete(emailToken);  // 토큰 삭제 (선택)

        return true;
    }

    // 이메일을 기반으로 아이디(Username)를 찾아 이메일로 전송하는 메서드
    public void sendUsernameToEmail(String email) {
        // 이메일로 사용자 찾기
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다.");
        }

        User user = optionalUser.get();

        // 이메일 전송
        String subject = "[Filmus] 아이디 찾기 결과 안내";
        String body = "회원님의 아이디는 다음과 같습니다: " + user.getUsername();

        emailService.sendEmail(email, subject, body);
    }

    // 임시 비밀번호 발급 및 전송 메서드
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다.");
        }
        User user = optionalUser.get();

        // 임시 비밀번호 생성 (UUID 앞부분 활용)
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(tempPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 이메일로 임시 비밀번호 전송
        String subject = "Filmus 임시 비밀번호 안내";
        String body = "요청하신 임시 비밀번호는 다음과 같습니다.\n\n" +
                tempPassword + "\n\n" +
                "로그인 후 반드시 비밀번호를 변경해주세요.";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    /**
     * 로그인된 사용자가 비밀번호를 변경하는 메서드
     *
     * @param username JWT 토큰에서 추출된 사용자명
     * @param request 현재 비밀번호와 새 비밀번호를 담은 요청 DTO
     */
    @Transactional
    public void changePassword(String username, ChangePasswordRequestDto request) {
        // 1. 사용자 정보 조회 (username은 인증된 사용자의 정보임)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 현재 비밀번호가 맞는지 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 3. 새 비밀번호를 암호화하여 저장
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);  // DB 반영
    }

}
