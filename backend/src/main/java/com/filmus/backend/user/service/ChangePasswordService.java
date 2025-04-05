package com.filmus.backend.user.service;

import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인된 사용자의 비밀번호 변경을 처리하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자의 현재 비밀번호를 검증하고 새 비밀번호로 변경합니다.
     */
    @Transactional
    public boolean changePassword(User user, String currentPassword, String newPassword) {

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // 새 비밀번호로 변경
        user.updatePassword(passwordEncoder.encode(newPassword));
        return true;
    }
}