package com.filmus.backend.user.service;

import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.dto.ChangePasswordRequestDto;
import com.filmus.backend.user.dto.UpdateUserInfoRequestDto;
import com.filmus.backend.user.dto.UserInfoResponseDto;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserInfoResponseDto getUserInfo(User user) {
        return new UserInfoResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getGender(),
                user.getBirthDate(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public void updateUserInfo(User user, UpdateUserInfoRequestDto request) {
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequestDto request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        tokenService.deleteRefreshToken(user);
        userRepository.delete(user);
    }

}
