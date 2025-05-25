package com.filmus.backend.recommend.homepage.dto;

import com.filmus.backend.recommend.weather.dto.WeatherRecommendedMovieDto;
import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.fixed.dto.FixedRecommendedMovieDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 홈화면 랜덤 추천 응답 DTO
 * - 추천 카테고리와 추천 영화 리스트를 포함
 */
@Getter
@Builder
@Schema(description = "홈화면 랜덤 추천 응답 DTO")
public class HomepageRecommendationDto {

    @Schema(description = "추천 카테고리", example = "fixed:romance")
    private String category;

    @Schema(description = "추천 영화 리스트 (weather/award/fixed 중 하나의 DTO 리스트)")
    private List<?> recommendations;
}
