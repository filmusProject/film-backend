package com.filmus.backend.recommend.weather.repository;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.recommend.weather.dto.MovieMatchScore;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * plot_keywords 기반 추천 영화 쿼리 구현체
 * → 일치 개수 계산 및 점수 기반 필터링
 */
@Repository
@RequiredArgsConstructor
public class WeatherMovieQueryRepositoryImpl implements WeatherMovieQueryRepository {

    private final EntityManager em;

    @Override
    public List<MovieMatchScore> findByKeywordMatchCount(List<String> keywords) {
        List<Movie> allMovies = em.createQuery("SELECT m FROM Movie m", Movie.class)
                .setMaxResults(1000)
                .getResultList();

        return allMovies.stream()
                .map(movie -> {
                    String plot = movie.getPlotKeywords();
                    if (plot == null || plot.isBlank()) return null;
                    long count = keywords.stream().filter(plot::contains).count();
                    return MovieMatchScore.of(movie, (int) count);
                })
                .filter(score -> score != null && score.getMatchCount() > 0)
                .toList();
    }
}
