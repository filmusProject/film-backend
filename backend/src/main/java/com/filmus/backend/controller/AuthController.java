package com.filmus.backend.controller;

import com.filmus.backend.dto.LoginRequestDto;
import com.filmus.backend.dto.SignupRequestDto;
import com.filmus.backend.dto.TokenResponseDto;
import com.filmus.backend.security.JwtTokenProvider;
import com.filmus.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // 모든 인증 관련 API의 공통 URL prefix
@Tag(name = "인증 API", description = "로그인, 로그아웃, 회원가입 기능을 제공하는 API")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자를 통해 의존성 주입 (AuthService, JwtTokenProvider)
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // [POST] 로그인 API
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        // 로그인 정보가 유효한 경우 → 토큰 생성
        if (authService.validateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtTokenProvider.createToken(loginRequest.getUsername());
            return ResponseEntity.ok(new TokenResponseDto(token)); // 토큰을 응답
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // [POST] 로그아웃 API
    @Operation(summary = "로그아웃", description = "JWT 토큰을 무효화하는 로그아웃 처리입니다. (실제로는 클라이언트에서 토큰 삭제)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // "Bearer " 접두사가 없으면 예외 발생
        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization 헤더는 'Bearer {토큰}' 형식이어야 합니다.");
        }

        // 접두사를 제거하고 실제 JWT 토큰만 추출
        String pureToken = token.substring(7);

        if (jwtTokenProvider.validateToken(pureToken)) {
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    // [POST] 회원가입 API
    @Operation(summary = "회원가입", description = "아이디, 이메일, 비밀번호 등 사용자 정보를 받아 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto requestDto) {
        try {
            authService.signup(requestDto); // 회원가입 처리
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다. 이메일 인증을 진행해주세요.");
        } catch (IllegalArgumentException e) {
            // 중복된 아이디 또는 이메일일 경우 예외 메시지를 반환
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
