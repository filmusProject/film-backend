package com.filmus.backend.service;

import com.filmus.backend.entity.User;
import com.filmus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * 비밀번호 재설정을 위한 임시 비밀번호 발급 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일로 임시 비밀번호를 전송하고 사용자 비밀번호를 변경합니다.
     */
    @Transactional
    public boolean resetPassword(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        // 임시 비밀번호 생성 (8자리 랜덤 UUID 앞부분)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        user.updatePassword(encodedPassword); // 사용자 엔티티에 비밀번호 변경 메서드 필요

        // 이메일 전송
        String subject = "[Filmus] 임시 비밀번호 안내";
        String content = "임시 비밀번호는 다음과 같습니다: " + tempPassword;
        emailService.sendEmail(email, subject, content);

        return true;
    }
}
