package com.filmus.backend.recommend.fixed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "추천 영화 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedRecommendedMovieDto {

    @Schema(description = "KMDB 영화 ID", example = "20230101")
    private String movieId;

    @Schema(description = "KMDB 영화 일련번호", example = "0001")
    private String movieSeq;

    @Schema(description = "영화 제목", example = "인셉션")
    private String title;

    @Schema(description = "개봉 연도", example = "2010")
    private String prodyear;

    @Schema(description = "장르", example = "액션, SF")
    private String genre;

    @Schema(description = "포스터 URL", example = "http://...")
    private String posterUrl;
}
