package com.filmus.backend.token.controller;

import com.filmus.backend.token.dto.TokenRequestDto;
import com.filmus.backend.token.dto.TokenResponseDto;
import com.filmus.backend.token.service.TokenService;
import com.filmus.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Tag(name = "Token", description = "토큰 재발급 API")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    public ResponseEntity<TokenResponseDto> reissueAccessToken(@RequestBody TokenRequestDto request) {
        String accessToken = tokenService.reissueAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(new TokenResponseDto("Bearer " + accessToken));
    }
}
