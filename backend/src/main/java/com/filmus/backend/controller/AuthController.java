package com.filmus.backend.controller;

import com.filmus.backend.dto.LoginRequestDto;
import com.filmus.backend.dto.TokenResponseDto;
import com.filmus.backend.security.JwtTokenProvider;
import com.filmus.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 사용자 인증을 담당하는 서비스 (비밀번호 검증 등)
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자를 통해 AuthService와 JwtTokenProvider를 주입받음
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 로그인 API 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        // 사용자의 아이디와 비밀번호 검증
        if (authService.validateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            // 검증 성공 시 JWT 토큰 생성
            String token = jwtTokenProvider.createToken(loginRequest.getUsername());

            // 클라이언트에게 JWT 토큰을 응답으로 반환
            return ResponseEntity.ok(new TokenResponseDto(token));
        } else {
            // 로그인 실패 시 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // 로그아웃 API 엔드포인트
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // 토큰에서 'Bearer ' 부분을 제거하고 실제 토큰만 추출
        String pureToken = token.replace("Bearer ", "");

        // JWT 토큰을 검증하고 로그아웃 처리
        if (jwtTokenProvider.validateToken(pureToken)) {
            // 클라이언트 측에서 토큰을 삭제하면 됨 (서버에서 따로 저장하는 것이 없기 때문)
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
