package com.filmus.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {

    @Schema(description = "사용자 아이디", example = "johndoe123")
    private String username;

    @Schema(description = "이메일 주소", example = "john@example.com")
    private String email;

    @Schema(description = "닉네임", example = "존도우")
    private String nickname;

    @Schema(description = "성별", example = "남성")
    private String gender;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "가입일", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;
}
