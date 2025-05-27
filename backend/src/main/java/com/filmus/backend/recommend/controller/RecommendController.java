package com.filmus.backend.recommend.controller;

import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.award.service.AwardRecommendationService;
import com.filmus.backend.recommend.fixed.dto.FixedRecommendedMovieDto;
import com.filmus.backend.recommend.fixed.service.FixedRecommendationService;
import com.filmus.backend.recommend.weather.service.WeatherRecommendationService;
import com.filmus.backend.recommend.weather.dto.WeatherRecommendedMovieDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
@Tag(name = "추천 API", description = "카테고리 기반, 영화제 기반, 날씨 기반 추천 API입니다.")
public class RecommendController {

    private final FixedRecommendationService fixedRecommendationService;         // 고정 추천
    private final AwardRecommendationService awardRecommendationService;         // 수상 추천
    private final WeatherRecommendationService weatherRecommendationService;     // 날씨 추천

    @Operation(summary = "전체 추천 영화 조회",
            description = "액션, 로맨스, 애니메이션 각 10개 + 영화제 출품작 10개 랜덤 + 날씨 기반 영화 20개 추천")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllRecommendations(
            @Parameter(description = "위도", example = "37.5665")
            @RequestParam(defaultValue = "37.5665") double lat,
            @Parameter(description = "경도", example = "126.9780")
            @RequestParam(defaultValue = "126.9780") double lon
    ) {
        // 고정 추천
        Map<String, List<FixedRecommendedMovieDto>> fixedRecommendations = fixedRecommendationService.getTodayRecommendations();

        // 수상 추천
        List<AwardRecommendedMovieDto> awardRecommendations = awardRecommendationService.getAwardRecommendations();

        // 날씨 추천
        List<WeatherRecommendedMovieDto> weatherRecommendations = weatherRecommendationService.getWeatherRecommendations(lat, lon);

        return ResponseEntity.ok(
                Map.of(
                        "fixedRecommendations", fixedRecommendations,
                        "awardRecommendations", awardRecommendations,
                        "weatherRecommendations", weatherRecommendations
                )
        );
    }
}
