package com.filmus.backend.recommend.weather.dto;

import com.filmus.backend.movie.entity.Movie;
import lombok.Builder;
import lombok.Getter;

/**
 * 추천 영화 + 일치 키워드 개수를 담는 DTO
 */
@Getter
@Builder
public class MovieMatchScore {
    private Movie movie;
    private int matchCount;

    public static MovieMatchScore of(Movie movie, int matchCount) {
        return MovieMatchScore.builder()
                .movie(movie)
                .matchCount(matchCount)
                .build();
    }
}
