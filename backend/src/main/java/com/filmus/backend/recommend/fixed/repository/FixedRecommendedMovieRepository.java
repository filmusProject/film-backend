package com.filmus.backend.recommend.fixed.repository;

import com.filmus.backend.recommend.fixed.entity.FixedRecommendedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FixedRecommendedMovieRepository extends JpaRepository<FixedRecommendedMovie, Long> {

    // 장르 포함 검색 (예: %액션%)
    List<FixedRecommendedMovie> findByGenreContaining(String keyword);

    // 오늘 날짜의 추천 영화 조회
    List<FixedRecommendedMovie> findByRecommendedDateAndGenreContaining(LocalDate date, String genre);

}

