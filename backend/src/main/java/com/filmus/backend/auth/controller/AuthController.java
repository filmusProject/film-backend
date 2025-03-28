package com.filmus.backend.auth.controller;

import com.filmus.backend.auth.dto.*;
import com.filmus.backend.auth.entity.User;
import com.filmus.backend.auth.security.JwtTokenProvider;
import com.filmus.backend.auth.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "회원가입, 로그인, 로그아웃, 이메일 인증 등 인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final EmailVerificationService emailVerificationService;
    private final FindUsernameService findUsernameService;
    private final ResetPasswordService resetPasswordService;
    private final ChangePasswordService changePasswordService;

    @Operation(summary = "회원가입", description = "아이디, 이메일, 비밀번호 등 사용자 정보를 받아 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        try {
            authService.signup(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다. 이메일 인증을 진행해주세요.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인 후, JWT 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"accessToken\":\"...\", \"refreshToken\":\"...\"}"))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        User user = authService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");

        String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());
        tokenService.saveRefreshToken(user, refreshToken);
        return ResponseEntity.ok(new TokenResponseDto(accessToken, refreshToken));
    }

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 사용해 액세스 토큰을 재발급합니다.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenRequestDto request) {
        String userId = jwtTokenProvider.getUserId(request.getRefreshToken());
        User user = authService.findUserById(Long.parseLong(userId));
        String savedToken = tokenService.getRefreshToken(user);
        if (savedToken == null || !savedToken.equals(request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken, request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하여 로그아웃 처리합니다.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDto request) {
        String userId = jwtTokenProvider.getUserId(request.getRefreshToken());
        User user = authService.findUserById(Long.parseLong(userId));
        tokenService.deleteRefreshToken(user);
        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(summary = "이메일 인증 확인", description = "전송된 이메일 인증 토큰을 확인합니다.")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean success = authService.verifyEmail(token, emailVerificationService);
        return success ? ResponseEntity.ok("이메일 인증이 완료되었습니다.")
                : ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증 링크입니다.");
    }

    @Operation(summary = "아이디 찾기", description = "이메일로 아이디를 전송합니다.")
    @PostMapping("/find-username")
    public ResponseEntity<String> findUsername(@RequestParam String email) {
        boolean sent = findUsernameService.sendUsernameToEmail(email);
        return sent ? ResponseEntity.ok("입력한 이메일로 아이디를 전송했습니다.")
                : ResponseEntity.badRequest().body("해당 이메일로 등록된 사용자가 없습니다.");
    }

    @Operation(summary = "임시 비밀번호 재설정", description = "이메일로 임시 비밀번호를 발급합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        boolean success = resetPasswordService.resetPassword(email);
        return success ? ResponseEntity.ok("입력하신 이메일로 임시 비밀번호를 전송했습니다.")
                : ResponseEntity.badRequest().body("해당 이메일로 등록된 사용자가 없습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        User user = (User) authentication.getPrincipal();
        boolean success = changePasswordService.changePassword(
                user.getUsername(), request.getCurrentPassword(), request.getNewPassword());
        return success ? ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.")
                : ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않습니다.");
    }
}
