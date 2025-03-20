package com.filmus.backend.dto;

// 사용자가 로그인 요청 시 보내는 데이터를 담는 DTO
public class LoginRequestDto {

    private String username; // 사용자의 아이디 또는 이메일
    private String password; // 사용자의 비밀번호

    // 기본 생성자 (Jackson 라이브러리가 객체를 생성할 때 필요)
    public LoginRequestDto() {}

    // 모든 필드를 초기화하는 생성자
    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter와 Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
