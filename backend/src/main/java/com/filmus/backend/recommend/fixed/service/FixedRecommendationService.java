package com.filmus.backend.recommend.fixed.service;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.recommend.fixed.dto.FixedRecommendedMovieDto;
import com.filmus.backend.recommend.fixed.entity.FixedRecommendedMovie;
import com.filmus.backend.recommend.fixed.repository.MovieQueryRepository;
import com.filmus.backend.recommend.fixed.repository.FixedRecommendedMovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixedRecommendationService {

    // 원본 영화 후보 조회용 리포지토리 (기존 movies 테이블)
    private final MovieQueryRepository movieQueryRepository;

    // 추천 결과 저장 및 조회용 리포지토리 (recommended_movies 테이블)
    private final FixedRecommendedMovieRepository fixedRecommendedMovieRepository;

    // 매일 자정 실행될 추천 생성 및 저장 메서드
    @Transactional
    public void saveDailyRecommendations() {
        LocalDate today = LocalDate.now();

        // 추천 카테고리 정의 (고정)
        Map<String, String> categoryMap = Map.of(
                "action", "액션",
                "romance", "로맨스",
                "anime", "애니"
        );

        // 각 카테고리별로 추천 생성
        categoryMap.forEach((key, keyword) -> {
            // 1. 기존 추천 삭제 (해당 날짜+장르)
            fixedRecommendedMovieRepository.deleteByRecommendedDateAndGenreContaining(today, keyword);

            // 2. 원본 영화 테이블에서 장르 포함 영화 조회
            List<Movie> candidates = movieQueryRepository.findMoviesByGenreKeyword(keyword);

            // 3. 랜덤 셔플 후 10개 선택
            Collections.shuffle(candidates);
            List<Movie> top10 = candidates.stream().limit(10).toList();

            // 4. 추천 테이블에 저장 (RecommendedMovie 엔티티 생성)
            top10.forEach(movie -> {
                FixedRecommendedMovie recommended = FixedRecommendedMovie.builder()
                        .movieId(movie.getMovieId())
                        .movieSeq(movie.getMovieSeq())
                        .title(movie.getTitle())
                        .prodyear(movie.getProdYear())
                        .genre(movie.getGenre())
                        .posterUrl(movie.getPosterUrl())
                        .recommendedDate(today)
                        .build();

                fixedRecommendedMovieRepository.save(recommended);
            });
        });
    }

    // 오늘 날짜의 추천 조회
    public Map<String, List<FixedRecommendedMovieDto>> getTodayRecommendations() {
        LocalDate today = LocalDate.now();

        Map<String, String> categoryMap = Map.of(
                "action", "액션",
                "romance", "로맨스",
                "anime", "애니"
        );

        Map<String, List<FixedRecommendedMovieDto>> result = new HashMap<>();

        categoryMap.forEach((key, keyword) -> {
            List<FixedRecommendedMovie> movies = fixedRecommendedMovieRepository
                    .findByRecommendedDateAndGenreContaining(today, keyword);

            result.put(key, movies.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        });

        return result;
    }

    // 엔티티 → DTO 변환 메서드
    private FixedRecommendedMovieDto convertToDto(FixedRecommendedMovie movie) {
        return FixedRecommendedMovieDto.builder()
                .movieId(movie.getMovieId())
                .movieSeq(movie.getMovieSeq())
                .title(movie.getTitle())
                .prodyear(movie.getProdyear())
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
