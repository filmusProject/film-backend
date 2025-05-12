// package: com.filmus.backend.recommend.award.service

package com.filmus.backend.recommend.award.service;

import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.award.entity.AwardRecommendedMovie;
import com.filmus.backend.recommend.award.repository.AwardRecommendedMovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwardRecommendationService {

    private static final int RECOMMENDATION_SIZE = 10; // 무조건 10개 추천

    private final AwardRecommendedMovieRepository awardRecommendedMovieRepository;

    // 전체 영화제 추천 목록 중에서 랜덤 10개 반환
    public List<AwardRecommendedMovieDto> getAwardRecommendations() {
        List<AwardRecommendedMovie> all = awardRecommendedMovieRepository.findAll(); // 전체 불러오기
        Collections.shuffle(all); // 무작위로 섞기

        return all.stream()
                .limit(RECOMMENDATION_SIZE) // 10개 자르기
                .map(AwardRecommendedMovieDto::from) // DTO로 변환
                .collect(Collectors.toList());
    }
}
