package com.filmus.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 회원 탈퇴 시 비밀번호를 전달받기 위한 요청 DTO입니다.
 */
@Getter
public class DeleteAccountRequestDto {

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(description = "현재 비밀번호", example = "mySecretPassword123")
    private String password;
}