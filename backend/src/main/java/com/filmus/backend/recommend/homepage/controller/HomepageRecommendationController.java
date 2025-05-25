package com.filmus.backend.recommend.homepage.controller;

import com.filmus.backend.recommend.homepage.dto.HomepageRecommendationDto;
import com.filmus.backend.recommend.homepage.service.HomepageRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 홈화면 진입 시 보여줄 랜덤 추천 API
 * - 고정, 날씨, 수상 추천 중 하나를 무작위로 선택
 */
@RestController
@RequestMapping("/api/recommend/home-random")
@RequiredArgsConstructor
@Tag(name = "홈 추천", description = "홈화면 진입 시 무작위 추천 제공 API")
public class HomepageRecommendationController {

    private final HomepageRecommendationService homepageRecommendationService;

    /**
     * 홈화면 랜덤 추천 조회
     * @return 랜덤 추천 결과 (카테고리 + 영화 리스트)
     */
    @GetMapping
    @Operation(summary = "홈화면 랜덤 추천", description = "고정, 날씨, 수상 추천 중 하나를 무작위로 선택하여 최대 10개 영화 추천")
    public ResponseEntity<HomepageRecommendationDto> getHomepageRecommendation() {
        return ResponseEntity.ok(homepageRecommendationService.getRandomRecommendation());
    }
}
