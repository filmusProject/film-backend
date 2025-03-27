package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 클라이언트가 로그아웃 요청 시 리프레시 토큰을 함께 전송하는 DTO입니다.
 */
public class LogoutRequestDto {

    @Schema(description = "로그아웃할 때 전송하는 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
