package com.filmus.backend.movie.dto;


// KMDB api를 요청할 때 검색어와 조건을 위한 DTO
public record SearchRequestDTO(
        String query,
        Integer page,
        String createDts,
        String createDte,
        String releaseDts,
        String releaseDte,
        String nation,
        String company,
        String genre,
        String use,
        String movieId,
        String movieSeq,
        String type,
        String title,
        String director,
        String actor,
        String staff,
        String keyword,
        String plot
) {}
