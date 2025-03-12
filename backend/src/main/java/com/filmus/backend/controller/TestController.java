package com.filmus.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 컨트롤러는 Swagger 설정을 테스트하기 위한 임시 API로, 나중에 삭제해도 됨
@RestController
@RequestMapping("/api/test")  // 이 컨트롤러의 기본 URL 경로 설정 (/api/test)
@Tag(name = "테스트 API", description = "Swagger 설정 확인을 위한 테스트 API")  // Swagger에서 이 컨트롤러를 '테스트 API'로 표시
public class TestController {

    // GET 요청을 처리하는 엔드포인트 설정 (http://localhost:8080/api/test/hello)
    @GetMapping("/hello")
    @Operation(summary = "Hello API", description = "Swagger 설정이 정상적으로 동작하는지 확인하는 API입니다.")
    public String hello() {
        return "Hello, Filmus!";  // API 호출 시 "Hello, Filmus!" 문자열 반환
    }
}

