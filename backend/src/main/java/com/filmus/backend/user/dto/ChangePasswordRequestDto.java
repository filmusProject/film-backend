package com.filmus.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "비밀번호 변경 요청 DTO")
@Getter
@Setter
public class ChangePasswordRequestDto {

    @Schema(description = "현재 비밀번호", example = "current1234")
    private String currentPassword; // 기존 비밀번호

    @Schema(description = "새 비밀번호", example = "newPassword5678")
    private String newPassword; // 새 비밀번호

    // === Lombok으로 Getter/Setter 자동 생성 ===

    @Override
    public String toString() {
        return "ChangePasswordRequestDto{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}