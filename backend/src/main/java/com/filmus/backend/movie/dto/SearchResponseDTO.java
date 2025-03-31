package com.filmus.backend.movie.dto;

import java.util.List;

// 프론트엔드에서 페이지네이션이 가능하도록 영화 리스트를 DTO로 래핑하여 전달

public record SearchResponseDTO(
        int totalCount,
        List<MovieSimpleDTO> movies
) {

    public record MovieSimpleDTO(
            String movieId,
            String movieSeq,
            String title,
            String year,
            String posterUrl
    ) {}
}