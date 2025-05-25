package com.filmus.backend.recommend.weather.dto;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.award.entity.AwardRecommendedMovie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 날씨 추천 응답용 영화 DTO
 */
@Getter
@Builder
public class WeatherRecommendedMovieDto {

    @Schema(description = "KMDB 영화 ID", example = "F")
    private String movieId;

    @Schema(description = "KMDB 영화 일련번호", example = "0001")
    private String movieSeq;

    @Schema(description = "영화 제목", example = "인셉션")
    private String title;

    @Schema(description = "개봉 연도", example = "2010")
    private String year;

    @Schema(description = "장르", example = "액션, SF")
    private String genre;

    @Schema(description = "포스터 URL", example = "http://...")
    private String posterUrl;

    public static WeatherRecommendedMovieDto from(Movie movie) {
        return WeatherRecommendedMovieDto.builder()
                .movieId(movie.getMovieId())
                .movieSeq(movie.getMovieSeq())
                .title(movie.getTitle())
                .year(movie.getProdYear())  // 주의: Movie는 prodYear로 되어 있을 것
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .build();
    }


}
