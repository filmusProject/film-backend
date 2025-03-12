package com.filmus.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이것은 나중에 프로그램 짤때 삭제해야함 그냥 테스트를 위한 프로그램임
@RestController
@RequestMapping("/api/test")
@Tag(name = "테스트 API", description = "Swagger 설정 확인을 위한 테스트 API")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Hello API", description = "Swagger 설정이 정상적으로 동작하는지 확인하는 API입니다.")
    public String hello() {
        return "Hello, Filmus!";
    }
}
