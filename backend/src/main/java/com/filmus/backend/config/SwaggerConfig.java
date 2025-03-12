package com.filmus.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Swagger 설정을 위한 설정 클래스임을 나타냄
@Configuration
public class SwaggerConfig {

    // OpenAPI 객체를 생성하여 Spring Bean으로 등록
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Filmus API")  // API 문서의 제목 설정
                        .description("Filmus 프로젝트 API 문서")  // API 문서 설명
                        .version("1.0"));  // API 버전 설정
    }
}

