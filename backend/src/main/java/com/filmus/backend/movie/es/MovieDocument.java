package com.filmus.backend.movie.es;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.filmus.backend.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDocument {
    @JsonProperty("docid")
    private String docid;

    @JsonProperty("movieId")
    private String movieId;

    @JsonProperty("movieSeq")
    private String movieSeq;

    @JsonProperty("title")
    private String title;

    @JsonProperty("titleEtc")
    private String titleEtc;

    @JsonProperty("directorNm")
    private String directorNm;

    @JsonProperty("actorNm")
    private String actorNm;

    @JsonProperty("nation")
    private String nation;

    @JsonProperty("company")
    private String company;

    @JsonProperty("prodYear")
    private String prodYear;

    @JsonProperty("plot")
    private String plot;

    @JsonProperty("runtime")
    private String runtime;

    @JsonProperty("rating")
    private String rating;

    @JsonProperty("genre")
    private String genre;

    @JsonProperty("kmdbUrl")
    private String kmdbUrl;

    @JsonProperty("type")
    private String type;

    @JsonProperty("isUse")
    private String isUse;

    @JsonProperty("ratedYn")
    private String ratedYn;

    @JsonProperty("repRatDate")
    private String repRatDate;

    @JsonProperty("repRlsDate")
    private String repRlsDate;

    @JsonProperty("keywords")
    private String keywords;

    @JsonProperty("posterUrl")
    private String posterUrl;

    @JsonProperty("stillUrl")
    private String stillUrl;

    @JsonProperty("vodClass")
    private String vodClass;

    @JsonProperty("vodUrl")
    private String vodUrl;

    @JsonProperty("awards1")
    private String awards1;

    @JsonProperty("awards2")
    private String awards2;

    @JsonProperty("plotKeywords")
    private String plotKeywords;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    public static MovieDocument fromEntity(Movie m) {
        return MovieDocument.builder()
                .docid(m.getDocid())
                .movieId(m.getMovieId())
                .movieSeq(m.getMovieSeq())
                .title(m.getTitle())
                .titleEtc(m.getTitleEtc())
                .directorNm(m.getDirectorNm())
                .actorNm(m.getActorNm())
                .nation(m.getNation())
                .company(m.getCompany())
                .prodYear(m.getProdYear())
                .plot(m.getPlot())
                .runtime(m.getRuntime())
                .rating(m.getRating())
                .genre(m.getGenre())
                .kmdbUrl(m.getKmdbUrl())
                .type(m.getType())
                .isUse(m.getIsUse())
                .ratedYn(m.getRatedYn())
                .repRatDate(m.getRepRatDate())
                .repRlsDate(m.getRepRlsDate())
                .keywords(m.getKeywords())
                .posterUrl(m.getPosterUrl())
                .stillUrl(m.getStillUrl())
                .vodClass(m.getVodClass())
                .vodUrl(m.getVodUrl())
                .awards1(m.getAwards1())
                .awards2(m.getAwards2())
                .plotKeywords(m.getPlotKeywords())
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null)
                .updatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt().toString() : null)
                .build();
    }
}

