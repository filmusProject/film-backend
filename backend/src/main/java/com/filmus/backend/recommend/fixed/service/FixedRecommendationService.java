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
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixedRecommendationService {

    private final MovieQueryRepository movieQueryRepository;
    private final FixedRecommendedMovieRepository fixedRecommendedMovieRepository;

    /**
     * 오늘 날짜의 고정 추천 영화 30개(카테고리별 10개)를 생성한다.
     */
    @Transactional
    public void saveDailyRecommendations() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        //  기존 추천 전체 삭제 (장르별 삭제가 아닌 전체 삭제)
        fixedRecommendedMovieRepository.deleteAll();

        // 추천할 고정 카테고리 맵
        Map<String, String> categoryMap = Map.of(
                "action", "액션",
                "romance", "로맨스",
                "anime", "애니"
        );

        //  장르별로 각각 10개씩 추천 생성 → 총 30개
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            String genreKey = entry.getKey();       // 예: action
            String genreKeyword = entry.getValue(); // 예: 액션

            // 장르 키워드 포함된 영화 후보 조회
            List<Movie> candidates = movieQueryRepository.findMoviesByGenreKeyword(genreKeyword);
            Collections.shuffle(candidates); // 랜덤 섞기

            List<Movie> top10 = candidates.stream()
                    .limit(10)
                    .toList();

            // 각 영화에 대해 추천 엔티티 생성
            for (Movie movie : top10) {
                FixedRecommendedMovie recommended = FixedRecommendedMovie.builder()
                        .movieId(movie.getMovieId())
                        .movieSeq(movie.getMovieSeq())
                        .title(movie.getTitle())
                        .year(movie.getProdYear() != null ? movie.getProdYear() : "미상")
                        .genre(movie.getGenre())
                        .posterUrl(movie.getPosterUrl())
                        .recommendedDate(today)
                        .build();

                fixedRecommendedMovieRepository.save(recommended);
            }
        }
    }

    /**
     * 오늘 날짜의 추천 결과를 카테고리별로 조회한다.
     * @return Map<카테고리명, 추천 DTO 리스트>
     */
    public Map<String, List<FixedRecommendedMovieDto>> getTodayRecommendations() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        Map<String, String> categoryMap = Map.of(
                "action", "액션",
                "romance", "로맨스",
                "anime", "애니"
        );

        Map<String, List<FixedRecommendedMovieDto>> result = new HashMap<>();

        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            String categoryKey = entry.getKey();       // 예: action
            String genreKeyword = entry.getValue();    // 예: 액션

            List<FixedRecommendedMovie> movies = fixedRecommendedMovieRepository
                    .findByRecommendedDateAndGenreContaining(today, genreKeyword);

            List<FixedRecommendedMovieDto> dtos = movies.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            result.put(categoryKey, dtos);
        }

        return result;
    }

    /**
     * 엔티티를 DTO로 변환
     */
    private FixedRecommendedMovieDto convertToDto(FixedRecommendedMovie movie) {
        return FixedRecommendedMovieDto.builder()
                .movieId(movie.getMovieId())
                .movieSeq(movie.getMovieSeq())
                .title(movie.getTitle())
                .year(movie.getYear())
                .genre(movie.getGenre())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}
