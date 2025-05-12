// package: com.filmus.backend.recommend.award.repository

package com.filmus.backend.recommend.award.repository;

import com.filmus.backend.recommend.award.entity.AwardRecommendedMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRecommendedMovieRepository extends JpaRepository<AwardRecommendedMovie, Long> {
    // 기본적인 findAll() 제공됨 → 전체 영화제 추천 영화 조회용
}
