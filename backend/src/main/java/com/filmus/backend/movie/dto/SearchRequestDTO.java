package com.filmus.backend.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SearchRequestDTO(

        @Schema(description = "검색어 (제목, 감독, 배우 등 자유 키워드)")
        String query,

        @Schema(description = "제작 시작일 (yyyyMMdd)")
        String createDts,

        @Schema(description = "제작 종료일 (yyyyMMdd)")
        String createDte,

        @Schema(description = "개봉 시작일 (yyyyMMdd)")
        String releaseDts,

        @Schema(description = "개봉 종료일 (yyyyMMdd)")
        String releaseDte,

        @Schema(description = "국가명 (예: 한국, 미국)")
        String nation,

        @Schema(description = "제작사")
        String company,

        @Schema(description = "장르 (예: 드라마, 코미디)")
        String genre,

        @Schema(description = "사용 여부 (Y/N)")
        String use,

        @Schema(description = "영화 고유 ID")
        String movieId,

        @Schema(description = "영화 고유 Seq")
        String movieSeq,

        @Schema(description = "자료 유형 (예: 극영화)")
        String type,

        @Schema(description = "영화 제목")
        String title,

        @Schema(description = "감독 이름")
        String director,

        @Schema(description = "배우 이름")
        String actor,

        @Schema(description = "스태프 이름")
        String staff,

        @Schema(description = "검색 키워드")
        String keyword,

        @Schema(description = "줄거리 내 검색어")
        String plot,

        @Schema(description = "페이지 번호 (1부터 시작)")
        Integer page
) {}