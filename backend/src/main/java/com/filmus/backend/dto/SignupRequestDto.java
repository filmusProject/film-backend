package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청을 위한 DTO 클래스입니다.
 */
@Getter
@Setter
public class SignupRequestDto {

    @Schema(description = "사용자 아이디", example = "filmuser01")
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String username;

    @Schema(description = "사용자 비밀번호 (최소 8자 이상)", example = "securePass123")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "이메일 주소", example = "filmuser01@example.com")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "닉네임", example = "영화덕후")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private String nickname;

    @Schema(description = "성별 (선택 입력)", example = "male")
    private String gender;

    @Schema(description = "생년월일 (선택 입력, yyyy-MM-dd 형식)", example = "1998-07-21")
    private String birthDate;
}
