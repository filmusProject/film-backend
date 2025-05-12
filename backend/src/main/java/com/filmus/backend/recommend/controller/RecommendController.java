package com.filmus.backend.recommend.controller;

import com.filmus.backend.recommend.fixed.dto.FixedRecommendedMovieDto;
import com.filmus.backend.recommend.fixed.service.FixedRecommendationService;
import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.award.service.AwardRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
@Tag(name = "추천 API", description = "카테고리 기반 및 영화제 기반 영화 추천 API입니다.")
public class RecommendController {

    private final FixedRecommendationService fixedRecommendationService;       // 고정 카테고리 추천 서비스
    private final AwardRecommendationService awardRecommendationService;       // 영화제 추천 서비스

    @Operation(summary = "전체 추천 영화 조회", description = "액션, 로맨스, 애니메이션 각 10개 + 영화제 출품작 10개 랜덤 추천")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllRecommendations() {
        Map<String, List<FixedRecommendedMovieDto>> fixedRecommendations = fixedRecommendationService.getTodayRecommendations();
        List<AwardRecommendedMovieDto> awardRecommendations = awardRecommendationService.getAwardRecommendations();

        return ResponseEntity.ok(
                Map.of(
                        "fixedRecommendations", fixedRecommendations,
                        "awardRecommendations", awardRecommendations
                )
        );
    }
}
