package com.filmus.backend.movie.dto;

// 프론트 반환을 위한 DTO
public record MovieDTO(
        Long id,
        String docid,
        String movieId,
        String movieSeq,
        String title,
        String titleEtc,
        String directorNm,
        String actorNm,
        String nation,
        String company,
        String prodYear,
        String plot,
        String runtime,
        String rating,
        String genre,
        String kmdbUrl,
        String type,
        String use,
        String ratedYn,
        String repRatDate,
        String repRlsDate,
        String keywords,
        String posterUrl,
        String stillUrl,
        String vodClass,
        String vodUrl,
        String awards1,
        String plotKeywords
) {}
