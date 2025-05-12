package com.filmus.backend.recommend.controller;


import com.filmus.backend.recommend.fixed.service.FixedRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class ReTestController {
    // 테스트용 이므로 나중에 완성후
    private final FixedRecommendationService recommendService;

    @GetMapping("/init")
    @Operation(summary = "추천 초기화", description = "오늘의 추천 영화를 강제로 새로 생성합니다.")
    public ResponseEntity<Void> initRecommendations() {
        recommendService.saveDailyRecommendations();
        return ResponseEntity.ok().build();
    }
}
