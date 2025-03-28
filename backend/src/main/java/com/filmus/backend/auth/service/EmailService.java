package com.filmus.backend.auth.service;

import com.filmus.backend.auth.entity.EmailVerificationToken;
import com.filmus.backend.auth.entity.User;
import com.filmus.backend.auth.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

@Service  // 서비스 레이어로 선언
@RequiredArgsConstructor  // final 필드 기반 생성자 자동 생성
public class EmailService {

    private final JavaMailSender mailSender;  // 이메일 전송을 담당할 객체
    private final EmailVerificationTokenRepository tokenRepository;

    @Value("${spring.mail.username}")
    private String fromAddress;  // application.yml에 설정한 발신자 주소

    // 이메일 인증 요청 처리
    @Transactional
    public void sendVerificationEmail(User user) {
        // UUID를 기반으로 인증 토큰 생성
        String token = UUID.randomUUID().toString();

        // 토큰의 만료 시간은 30분 후로 설정
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(30);

        // 토큰 객체 생성 및 저장
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expirationTime)
                .build();

        tokenRepository.save(verificationToken);

        // 인증 링크 생성
        String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + token;

        // 이메일 본문 구성
        String subject = "Filmus 회원가입 이메일 인증";
        String message = """
                Filmus에 가입해주셔서 감사합니다!
                
                아래 링크를 클릭하여 이메일 인증을 완료해주세요:
                
                """ + verificationUrl;

        // 메일 객체 생성
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());          // 수신자
        mailMessage.setSubject(subject);             // 제목
        mailMessage.setText(message);                // 본문
        mailMessage.setFrom(fromAddress);            // 발신자

        // 메일 전송
        mailSender.send(mailMessage);
    }

    // 재사용 가능한 이메일 전송 메서드 (아이디 찾기, 비밀번호 초기화 등 일반 메일 전송용)
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);             // 수신자
            helper.setSubject(subject);   // 제목
            helper.setText(text, false);  // 본문 내용 (텍스트)
            helper.setFrom(fromAddress);  // 발신자 설정

            mailSender.send(message);     // 메일 전송 실행
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 중 오류 발생: " + e.getMessage());
        }
    }
}
