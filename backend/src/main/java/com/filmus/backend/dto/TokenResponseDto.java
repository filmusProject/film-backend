package com.filmus.backend.dto;

// 로그인 성공 시 클라이언트에게 반환할 JWT 토큰을 담는 DTO
public class TokenResponseDto {

    private String token; // 발급된 JWT 토큰

    // 기본 생성자
    public TokenResponseDto() {}

    // 모든 필드를 초기화하는 생성자
    public TokenResponseDto(String token) {
        this.token = token;
    }

    // Getter와 Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
