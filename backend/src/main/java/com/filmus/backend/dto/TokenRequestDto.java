package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 클라이언트가 리프레시 토큰을 서버로 전송할 때 사용하는 요청 DTO입니다.
 * - 액세스 토큰 재발급 등에 사용됩니다.
 */
public class TokenRequestDto {

    @Schema(description = "클라이언트가 보유한 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}