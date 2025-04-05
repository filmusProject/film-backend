package com.filmus.backend.user.controller;


import com.filmus.backend.user.dto.ResetPasswordRequestDto;
import com.filmus.backend.user.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/password")
@RequiredArgsConstructor
@Tag(name = "Password", description = "비밀번호 초기화 API")
public class PasswordResetController {

    private final ResetPasswordService resetPasswordService;

    @Operation(summary = "비밀번호 재설정 요청", description = "입력한 이메일로 임시 비밀번호를 발송합니다.")
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        boolean success = resetPasswordService.resetPassword(request.getEmail());

        if (success) {
            return ResponseEntity.ok("임시 비밀번호가 이메일로 전송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("해당 이메일로 가입된 사용자가 없습니다.");
        }
    }
}