package com.filmus.backend.oauth.handler;

import com.filmus.backend.common.util.CookieUtil;
import com.filmus.backend.token.service.JwtTokenProvider;
import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * 실제 서비스용 OAuth2 로그인 성공 핸들러
 * - 로그인 성공 시 JWT 쿠키를 생성하고
 * - 프론트엔드로 리디렉션 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;   // JWT 토큰 생성기
    private final TokenService tokenService;           // RefreshToken 저장 서비스
    private final UserRepository userRepository;       // 사용자 조회용
    private final CookieUtil cookieUtil;               // 공통 쿠키 유틸리티

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;                        // 로그인 성공 후 리디렉션될 프론트엔드 URI

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("[OAuth2] 소셜 로그인 성공!");

        // OAuth2 인증 정보를 Principal로부터 꺼냄
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // providerId는 Kakao 고유 식별자 (id)
        Object rawId = oAuth2User.getAttribute("id");
        if (rawId == null) {
            log.error("[OAuth2] providerId (id) 누락");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "소셜 사용자 ID가 유효하지 않습니다.");
            return;
        }
        String providerId = rawId.toString();
        String provider = "KAKAO";  // 현재는 Kakao만 지원

        // DB에서 해당 소셜 계정이 등록된 유저를 찾음
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

        // 리프레시 토큰 저장 (DB or Redis 등)
        tokenService.saveRefreshToken(user, refreshToken);

        // ✅ 쿠키 생성 및 응답에 추가
        Cookie accessTokenCookie = cookieUtil.createSecureHttpOnlyCookie("accessToken", accessToken, 3600);       // 1시간
        Cookie refreshTokenCookie = cookieUtil.createSecureHttpOnlyCookie("refreshToken", refreshToken, 1209600); // 14일
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // ✅ 리디렉션
        log.info("[OAuth2] 리디렉션: {}", redirectUri);
        response.sendRedirect(redirectUri);
    }
}
