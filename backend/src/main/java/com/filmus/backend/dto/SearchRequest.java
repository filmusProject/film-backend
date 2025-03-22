package com.filmus.backend.dto;

public record SearchRequest(
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
