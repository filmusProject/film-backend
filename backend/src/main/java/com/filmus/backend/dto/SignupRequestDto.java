package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원가입 요청 DTO")
@Getter @Setter
public class SignupRequestDto {

    @Schema(description = "사용자 아이디", example = "filmlover123", required = true)
    private String username;

    @Schema(description = "비밀번호", example = "securePassword123!", required = true)
    private String password;

    @Schema(description = "이메일", example = "user@example.com", required = true)
    private String email;

    @Schema(description = "닉네임", example = "영화덕후", required = true)
    private String nickname;

    @Schema(description = "성별 (M/F/기타)", example = "F", required = false)
    private String gender;

    @Schema(description = "생년월일", example = "1999-07-01", required = false)
    private String birthDate;
}
