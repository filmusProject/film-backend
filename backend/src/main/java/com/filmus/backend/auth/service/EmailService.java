package com.filmus.backend.auth.service;


import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.auth.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Slf4j
@Service  // 서비스 레이어로 선언
@RequiredArgsConstructor  // final 필드 기반 생성자 자동 생성
public class EmailService {

    private final JavaMailSender mailSender;  // 이메일 전송을 담당할 객체
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailVerificationService emailVerificationService;

    @Value("${spring.mail.username}")
    private String fromAddress;  // application.yml에 설정한 발신자 주소

    // 이메일 인증 요청 처리
    @Transactional
    public void sendVerificationEmail(User user) {

        // 메일 전송
        try {
            String token = emailVerificationService.generateToken(user);
            // 인증 링크 생성
            String verificationUrl = "http://api.filmus.o-r.kr/api/auth/verify-email?token=" + token;

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
            mailSender.send(mailMessage);
            log.info("메일 발송 성공", user.getEmail());
        } catch (Exception e) {
            log.error("메일 발송 실패", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
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
            log.error("일반 메일 발송 실패", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
