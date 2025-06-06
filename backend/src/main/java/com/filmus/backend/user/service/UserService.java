package com.filmus.backend.user.service;

import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.oauth.service.KakaoUnlinkService;
import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.dto.ChangePasswordRequestDto;
import com.filmus.backend.user.dto.UpdateUserInfoRequestDto;
import com.filmus.backend.user.dto.UserInfoResponseDto;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
    private final KakaoUnlinkService kakaoUnlinkService;

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
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) {
        tokenService.deleteRefreshToken(user);
        userRepository.delete(user);
    }
//    @Transactional
//    public void deleteAccountWithPassword(User user, String password, HttpServletResponse response) {
//        if (user.getProvider() == null) { // 일반 로그인 사용자만 비밀번호 확인 필요
//            if (!passwordEncoder.matches(password, user.getPassword())) {
//                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//            }
//        } else if ("KAKAO".equalsIgnoreCase(user.getProvider())) {
//            kakaoUnlinkService.unlink(user); // 소셜 연동 해제 (실패 시 무시 가능)
//        }
//
//        tokenService.deleteRefreshToken(user);
//        response.addHeader("Set-Cookie", buildExpiredCookie("accessToken"));
//        response.addHeader("Set-Cookie", buildExpiredCookie("refreshToken"));
//        userRepository.delete(user);
//    }
//
//    private String buildExpiredCookie(String name) {
//        return String.format("%s=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None", name);
//    }

}
