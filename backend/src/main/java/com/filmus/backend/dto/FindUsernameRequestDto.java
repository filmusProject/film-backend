package com.filmus.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "아이디 찾기 요청에 사용되는 DTO")
@Getter
@Setter
public class FindUsernameRequestDto {

    @Schema(description = "가입 시 사용한 이메일", example = "testuser@example.com")
    private String email; // 사용자 이메일 입력값
}
