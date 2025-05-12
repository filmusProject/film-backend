package com.filmus.backend.movie.dto;

import java.util.List;

public record NlpKeywordResponseDTO (
        List<ExtractedKeyword> extracted_keywords,
        String summary
){
    public record ExtractedKeyword (
            String category,
            String keyword,
            Double score,
            String subcategory
    ){}
}
