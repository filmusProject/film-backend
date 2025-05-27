package com.filmus.backend.recommend.homepage.service;

import com.filmus.backend.recommend.weather.service.WeatherRecommendationService;
import com.filmus.backend.recommend.award.service.AwardRecommendationService;
import com.filmus.backend.recommend.fixed.service.FixedRecommendationService;
import com.filmus.backend.recommend.homepage.dto.HomepageRecommendationDto;
import com.filmus.backend.recommend.weather.dto.WeatherRecommendedMovieDto;
import com.filmus.backend.recommend.award.dto.AwardRecommendedMovieDto;
import com.filmus.backend.recommend.fixed.dto.FixedRecommendedMovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 홈화면 진입 시 보여줄 랜덤 추천을 생성하는 서비스
 * - 날씨, 수상, 고정 추천 중 하나를 무작위로 선택
 * - 각 추천은 최대 5개만 제공
 */
@Service
@RequiredArgsConstructor
public class HomepageRecommendationService {

    private final WeatherRecommendationService weatherRecommendationService;
    private final AwardRecommendationService awardRecommendationService;
    private final FixedRecommendationService fixedRecommendationService;

    private static final int LIMIT = 10;  // 추천 개수 제한
    private static final List<String> FIXED_CATEGORIES = List.of("romance", "action", "anime");

    /**
     * 홈화면용 랜덤 추천 생성
     * @return 추천 카테고리 및 영화 리스트
     */
    public HomepageRecommendationDto getRandomRecommendation() {
        // 추천 종류 중 하나를 무작위 선택
        List<String> candidateTypes = List.of("weather", "award", "fixed");
        String selectedType = pickRandom(candidateTypes);

        switch (selectedType) {
            case "weather" -> {
                // 날씨 추천 (서울 고정 위치 기준)
                List<WeatherRecommendedMovieDto> weather = weatherRecommendationService.getWeatherRecommendations(37.5665, 126.9780);
                return HomepageRecommendationDto.builder()
                        .category("weather")
                        .recommendations(limitList(weather))
                        .build();
            }
            case "award" -> {
                // 수상 추천 (전체 리스트 중 무작위 10개)
                List<AwardRecommendedMovieDto> award = awardRecommendationService.getAwardRecommendations();
                Collections.shuffle(award);
                return HomepageRecommendationDto.builder()
                        .category("award")
                        .recommendations(limitList(award))
                        .build();
            }
            case "fixed" -> {
                // 고정 추천 중 장르 하나 무작위 선택
                String subCategory = pickRandom(FIXED_CATEGORIES);
                List<FixedRecommendedMovieDto> fixed = fixedRecommendationService.getTodayRecommendations().get(subCategory);
                return HomepageRecommendationDto.builder()
                        .category("fixed:" + subCategory)
                        .recommendations(limitList(fixed))
                        .build();
            }
            default -> throw new IllegalStateException("Unknown recommendation type: " + selectedType);
        }
    }

    /**
     * 주어진 리스트에서 최대 LIMIT 개수만 추출
     */
    private <T> List<T> limitList(List<T> list) {
        return list.stream().limit(LIMIT).toList();
    }

    /**
     * 리스트에서 랜덤으로 하나 선택
     */
    private String pickRandom(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
