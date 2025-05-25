// package: com.filmus.backend.recommend.award.dto

package com.filmus.backend.recommend.award.dto;

import com.filmus.backend.recommend.award.entity.AwardRecommendedMovie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "수상내역 기반 추천 영화 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardRecommendedMovieDto {

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


    public static AwardRecommendedMovieDto from(AwardRecommendedMovie movie) {
        return AwardRecommendedMovieDto.builder()
                .movieId(movie.getMovieId())
                .movieSeq(movie.getMovieSeq())
                .title(movie.getTitle())
                .year(movie.getYear())
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .build();
    }


}
