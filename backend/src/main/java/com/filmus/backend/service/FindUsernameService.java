package com.filmus.backend.service;

import com.filmus.backend.entity.User;
import com.filmus.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 사용자의 이메일을 기반으로 아이디를 찾고 전송하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class FindUsernameService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * 입력된 이메일에 해당하는 사용자에게 아이디를 전송합니다.
     */
    public boolean sendUsernameToEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            String username = optionalUser.get().getUsername();
            String subject = "[Filmus] 아이디 찾기 결과";
            String content = "회원님의 아이디는 다음과 같습니다: " + username;
            emailService.sendEmail(email, subject, content);
            return true;
        }

        return false; // 해당 이메일을 가진 사용자가 없음
    }
}