// package: com.filmus.backend.recommend.award.dto

package com.filmus.backend.recommend.award.dto;

import com.filmus.backend.recommend.award.entity.AwardRecommendedMovie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AwardRecommendedMovieDto {

    @Schema(description = "KMDB 영화 ID(PK)", example = "01")
    private final Long movieId;       // 영화 ID

    @Schema(description = "영화 제목", example = "인셉션")
    private final String title;       // 영화 제목

    @Schema(description = "포스터 URL", example = "http://...")
    private final String posterUrl;   // 영화 포스터 URL

    @Builder
    public AwardRecommendedMovieDto(Long movieId, String title, String posterUrl) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
    }

    // Entity → DTO 변환 메서드
    public static AwardRecommendedMovieDto from(AwardRecommendedMovie entity) {
        return AwardRecommendedMovieDto.builder()
                .movieId(entity.getMovie().getId())
                .title(entity.getMovie().getTitle())
                .posterUrl(entity.getMovie().getPosterUrl())
                .build();
    }
}
