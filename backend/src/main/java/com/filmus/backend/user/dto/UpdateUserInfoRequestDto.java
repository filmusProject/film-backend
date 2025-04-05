package com.filmus.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateUserInfoRequestDto {


    @Schema(description = "변경할 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "변경할 성별", example = "남성")
    private String gender;

    @Schema(description = "변경할 생년월일", example = "2000-05-20")
    private LocalDate birthDate;
}
