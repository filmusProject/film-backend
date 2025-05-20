package com.filmus.backend.recommend.weather.repository;

import com.filmus.backend.recommend.weather.dto.MovieMatchScore;
import java.util.List;

/**
 * plot_keywords 기반 추천 영화 쿼리
 * → 일치 키워드 수를 기반으로 추천 정확도를 높임
 */
public interface WeatherMovieQueryRepository {

    /**
     * plot_keywords에 포함된 키워드 개수를 계산하여 점수를 반환
     *
     * @param keywords 날씨 기반 추천 키워드
     * @return 일치 점수를 포함한 추천 후보 리스트
     */
    List<MovieMatchScore> findByKeywordMatchCount(List<String> keywords);
}
