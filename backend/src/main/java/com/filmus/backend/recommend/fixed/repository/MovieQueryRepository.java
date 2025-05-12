package com.filmus.backend.recommend.fixed.repository;

import com.filmus.backend.movie.entity.Movie;

import java.util.List;

/**
 * 영화 추천용 장르 기반 커스텀 조회를 위한 Repository 인터페이스
 */
public interface MovieQueryRepository {

    /**
     * 특정 장르 키워드가 포함된 영화 목록을 조회한다.
     * 예: keyword = "액션" → genre LIKE '%액션%'
     *
     * @param keyword 장르 키워드
     * @return 해당 키워드가 포함된 영화 리스트
     */
    List<Movie> findMoviesByGenreKeyword(String keyword);
}
