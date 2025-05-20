package com.filmus.backend.recommend.weather.service;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.recommend.weather.client.WeatherClient;
import com.filmus.backend.recommend.weather.dto.MovieMatchScore;
import com.filmus.backend.recommend.weather.dto.WeatherRecommendedMovieDto;
import com.filmus.backend.recommend.weather.repository.WeatherMovieQueryRepository;
import com.filmus.backend.recommend.weather.util.WeatherKeywordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 날씨 기반 영화 추천 서비스
 * → 일치 키워드 수 기준 정렬 + 그룹별 랜덤화 전략 적용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherRecommendationService {

    private final WeatherClient weatherClient;
    private final WeatherMovieQueryRepository weatherMovieQueryRepository;

    private static final int DB_FETCH_LIMIT = 500;
    private static final int RECOMMEND_LIMIT = 20;

    @Cacheable(value = "weatherRecommendation", key = "#lat + '_' + #lon")
    public List<WeatherRecommendedMovieDto> getWeatherRecommendations(double lat, double lon) {
        String cacheKey = lat + "_" + lon;
        log.info("[캐시 KEY] {}", cacheKey);
        log.info("[캐시 미적용] 날씨 추천 새로 생성: lat={}, lon={}", lat, lon);

        String weatherMain = weatherClient.getCurrentWeatherMainByLatLon(lat, lon);
        log.info("[날씨 상태] {}", weatherMain);

        List<String> keywords = WeatherKeywordMapper.getKeywords(weatherMain);
        log.info("[매핑된 추천 키워드] {}", keywords);

        List<MovieMatchScore> rawMatches = weatherMovieQueryRepository.findByKeywordMatchCount(keywords);
        log.info("[추천 대상 영화 수] {}개", rawMatches.size());

        List<MovieMatchScore> finalSorted = sortWithRandomness(rawMatches);

        return finalSorted.stream()
                .map(score -> WeatherRecommendedMovieDto.from(score.getMovie()))
                .limit(RECOMMEND_LIMIT)
                .toList();
    }

    /**
     * 일치 키워드 수 기준 정렬 + 같은 점수 내에서는 랜덤 섞기
     */
    private List<MovieMatchScore> sortWithRandomness(List<MovieMatchScore> rawList) {
        // 점수로 그룹화
        Map<Integer, List<MovieMatchScore>> grouped = rawList.stream()
                .collect(Collectors.groupingBy(MovieMatchScore::getMatchCount));

        // 점수 내림차순 정렬 + 각 그룹 랜덤 섞기
        return grouped.entrySet().stream()
                .sorted((e1, e2) -> e2.getKey() - e1.getKey())
                .flatMap(entry -> {
                    List<MovieMatchScore> group = entry.getValue();
                    Collections.shuffle(group);
                    return group.stream();
                })
                .toList();
    }
}
