package com.filmus.backend.recommend.weather.dto;

import com.filmus.backend.movie.entity.Movie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 날씨 추천 응답용 영화 DTO
 */
@Getter
@Builder
public class WeatherRecommendedMovieDto {

    @Schema(description = "영화 ID")
    private Long id;

    @Schema(description = "영화 제목")
    private String title;

    @Schema(description = "포스터 이미지 URL")
    private String posterUrl;

    /**
     * Movie 엔티티 → Weather DTO 변환 메서드
     */
    public static WeatherRecommendedMovieDto from(Movie movie) {
        return WeatherRecommendedMovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
