package com.filmus.backend.token.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 클라이언트에게 JWT 액세스/리프레시 토큰을 응답할 때 사용하는 DTO입니다.
 */
@Getter
@AllArgsConstructor
public class TokenResponseDto {

    @Schema(description = "새로운 액세스 토큰", example = "Bearer eyJhbGciOiJIUz...")
    private String accessToken;
}