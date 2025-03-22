package com.filmus.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 📌 Swagger 기본 문서 설명 + 보안 설정을 함께 정의하는 어노테이션
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Filmus API",                       // Swagger 문서 제목
                version = "1.0",                            // 버전 정보
                description = "Filmus 프로젝트 API 문서"     // 설명
        ),
        security = @SecurityRequirement(name = "JWT")   // 전역 JWT 인증 설정
)
@SecurityScheme(
        name = "JWT",                // Swagger UI에서 사용할 스키마 이름
        type = SecuritySchemeType.HTTP,  // HTTP 방식 인증
        scheme = "bearer",               // "Bearer" 토큰 형식
        bearerFormat = "JWT"             // Swagger UI에 표기되는 형식 (설명용)
)
public class SwaggerConfig {

    // OpenAPI 객체를 생성하여 Spring Bean으로 등록
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI();
        // 추가 설정 가능 (예: 서버 주소 설정 등)
    }
}
