package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "비밀번호 재설정을 요청할 때 사용하는 DTO")
@Getter
@Setter
public class ResetPasswordRequestDto {

    @Schema(description = "비밀번호를 재설정할 사용자 이메일", example = "user@example.com")
    private String email; // 사용자 이메일 입력값

    // === Lombok을 통해 Getter, Setter 자동 생성 ===

    // 기본 생성자 및 전체 필드를 받는 생성자가 필요한 경우 아래 주석 해제
    // public ResetPasswordRequestDto() {}
    // public ResetPasswordRequestDto(String email) {
    //     this.email = email;
    // }

    @Override
    public String toString() {
        return "ResetPasswordRequestDto{" +
                "email='" + email + '\'' +
                '}';
    }
}
