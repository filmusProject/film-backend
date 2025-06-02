package com.filmus.backend.oauth.handler;

import com.filmus.backend.token.service.JwtTokenProvider;
import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 실제 서비스용 OAuth2 로그인 성공 핸들러
 * - JWT 쿠키 저장
 * - 프론트엔드로 리디렉션
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;   // JWT 토큰 생성기
    private final TokenService tokenService;           // RefreshToken 저장 서비스
    private final UserRepository userRepository;       // 사용자 조회용

    // 리디렉션할 프론트엔드 URI (yml에서 주입)
    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("[OAuth2] 소셜 로그인 성공!");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // providerId 추출
        Object rawId = oAuth2User.getAttribute("id");
        if (rawId == null) {
            log.error("[OAuth2] providerId (id) 누락");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "소셜 사용자 ID가 유효하지 않습니다.");
            return;
        }
        String providerId = rawId.toString();
        String provider = "KAKAO";  // 현재는 Kakao만 처리

        // DB에서 사용자 조회
        Optional<User> optionalUser = userRepository.findByProviderAndProviderId(provider, providerId);
        if (optionalUser.isEmpty()) {
            log.error("[OAuth2] 사용자 DB 조회 실패: provider={}, providerId={}", provider, providerId);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "사용자 정보가 존재하지 않습니다.");
            return;
        }

        User user = optionalUser.get();

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // refreshToken 저장 (DB or Redis)
        tokenService.saveRefreshToken(user, refreshToken);

        // JWT를 쿠키로 저장
        response.addHeader("Set-Cookie", buildCookie("accessToken", accessToken, 3600));       // 1시간
        response.addHeader("Set-Cookie", buildCookie("refreshToken", refreshToken, 1209600));  // 14일

        // 프론트엔드로 리디렉션
        log.info("[OAuth2] 리디렉션: {}", redirectUri);
        response.sendRedirect(redirectUri);
    }

    /**
     * JWT 토큰을 Secure + HttpOnly + SameSite 쿠키로 설정
     */
    private String buildCookie(String name, String value, int maxAgeSeconds) {
        if (value == null) {
            throw new IllegalArgumentException(name + " 쿠키 값이 null입니다.");
        }

        return String.format(
                "%s=%s; Max-Age=%d; Path=/; Secure; HttpOnly; SameSite=None",
                name,
                URLEncoder.encode(value, StandardCharsets.UTF_8),
                maxAgeSeconds
        );
    }
}
