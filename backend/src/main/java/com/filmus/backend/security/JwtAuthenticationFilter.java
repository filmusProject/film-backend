package com.filmus.backend.security;

import com.filmus.backend.token.service.JwtTokenProvider;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 주석을 추가할 것 그리고 쓸데없는 코드는 삭제할 것 이 코드가 제일 오류 많이남
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        log.info("[JwtFilter] 요청 경로: {}", path);
        boolean shouldNotFilter = path.equals("/api/auth/login") || path.equals("/api/auth/signup");
        log.info("[JwtFilter] 필터 적용 여부: {}", !shouldNotFilter);
        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        log.info("[JwtFilter] Authorization 헤더에서 추출한 토큰: {}", token);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            try {
                String userId = jwtTokenProvider.getUserId(token); // "4" 같은 user PK
                log.info("[JwtFilter] JWT에서 추출한 사용자 ID: {}", userId);

                User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
                log.info("[JwtFilter] DB에서 조회한 사용자: {}", user != null ? user.getUsername() : "null");

                if (user != null) {
                    // ✅ 여기서 UserDetailsImpl로 감싸서 Principal 설정
                    UserDetailsImpl userDetails = new UserDetailsImpl(user);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities() // "ROLE_USER" 등
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("[JwtFilter] SecurityContext에 인증 정보 설정 완료. 인증된 사용자: {}",
                            SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                } else {
                    log.warn("[JwtFilter] 사용자를 찾을 수 없음. userId: {}", userId);
                }
            } catch (Exception e) {
                log.error("[JwtFilter] 인증 중 예외 발생: {}", e.getMessage(), e);
            }
        } else {
            log.debug("[JwtFilter] 유효하지 않은 토큰 또는 Authorization 헤더 없음");
            if (token != null) {
                log.debug("[JwtFilter] 토큰 유효성 검사 결과: {}", jwtTokenProvider.validateToken(token));
            }
        }

        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("[JwtFilter] Authorization 헤더: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거하고 실제 토큰만 반환
        }
        return null;
    }
}
