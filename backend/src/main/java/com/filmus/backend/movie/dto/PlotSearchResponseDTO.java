package com.filmus.backend.movie.dto;


import java.util.List;

public record PlotSearchResponseDTO(
        List<NlpKeywordResponseDTO.ExtractedKeyword> extractedKeywords,
        String summary,
        List<MovieMatchDTO> matchedMovies
) {}