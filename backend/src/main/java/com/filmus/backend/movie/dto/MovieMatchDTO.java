package com.filmus.backend.movie.dto;

import java.util.List;

public record MovieMatchDTO (
        Long movieId,
        String movieSeq,
        String title,
        String year,
        String posterUrl,
        List<String> matchedKeywords
) {
}
