package com.filmus.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host}")
    private String host;  // SMTP 서버

    @Value("${mail.port}")
    private int port;  // 포트

    @Value("${mail.username}")
    private String username;  // 발신자 이메일

    @Value("${mail.password}")
    private String password;  // 앱 비밀번호

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(host);  // SMTP 서버 설정
        mailSender.setPort(port);  // 포트 설정
        mailSender.setUsername(username);  // 발신자 이메일
        mailSender.setPassword(password);  // 앱 비밀번호

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");  // 인증 필요
        props.put("mail.smtp.starttls.enable", "true");  // TLS 사용
        props.put("mail.debug", "true");  // 디버깅 활성화 (로그 확인)

        return mailSender;
    }
}