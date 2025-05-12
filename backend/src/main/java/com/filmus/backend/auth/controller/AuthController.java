package com.filmus.backend.auth.controller;

import com.filmus.backend.auth.dto.*;
import com.filmus.backend.security.UserDetailsImpl;
import com.filmus.backend.token.service.JwtTokenProvider;
import com.filmus.backend.auth.service.*;
import com.filmus.backend.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        String accessToken = authService.login(loginRequest, response);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body("로그인 성공");
    }




    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하여 로그아웃 처리합니다.",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletResponse response) {
        if (userDetails == null) {
            // 이미 로그아웃된 상태거나 인증이 없음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("이미 로그아웃된 상태이거나 유효하지 않은 토큰입니다.");
        }
        authService.logout(userDetails.getUser(),response);
        return ResponseEntity.ok("로그아웃 성공");
    }




    @Operation(summary = "이메일 인증 확인", description = "전송된 이메일 인증 토큰을 확인합니다.")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean success = authService.verifyEmail(token, emailVerificationService);
        return success ? ResponseEntity.ok("이메일 인증이 완료되었습니다.")
                : ResponseEntity.badRequest().body("유효하지 않거나 만료된 인증 링크입니다.");
    }


    /// ---- 미로그인 상태에서 사용자 정보 요청을 위한 api

    @Operation(summary = "아이디 찾기", description = "이메일로 아이디를 전송합니다.")
    @PostMapping("/find-username")
    public ResponseEntity<String> findUsername(@RequestParam String email) {
        boolean sent = findUsernameService.sendUsernameToEmail(email);
        return sent ? ResponseEntity.ok("입력한 이메일로 아이디를 전송했습니다.")
                : ResponseEntity.badRequest().body("해당 이메일로 등록된 사용자가 없습니다.");
    }

    @PostMapping("/admin/signup")
    @PreAuthorize("hasRole('ADMIN')") // Spring Security 설정 필요
    public void adminSignup(@RequestBody @Valid SignupRequestDto request) {
        authService.adminSignup(request);
    }




//    @Operation(summary = "임시 비밀번호 재설정", description = "이메일로 임시 비밀번호를 발급합니다.")
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestParam String email) {
//        boolean success = resetPasswordService.resetPassword(email);
//        return success ? ResponseEntity.ok("입력하신 이메일로 임시 비밀번호를 전송했습니다.")
//                : ResponseEntity.badRequest().body("해당 이메일로 등록된 사용자가 없습니다.");
//    }
//
//
//
//    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.",
//            security = @SecurityRequirement(name = "BearerAuth"))
//    @PutMapping("/change-password")
//    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequestDto request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
//        }
//
//        User user = (User) authentication.getPrincipal();
//        boolean success = changePasswordService.changePassword(
//                user.getUsername(), request.getCurrentPassword(), request.getNewPassword());
//        return success ? ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.")
//                : ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않습니다.");
//    }
}
