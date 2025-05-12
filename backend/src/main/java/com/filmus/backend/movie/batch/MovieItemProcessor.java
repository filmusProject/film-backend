package com.filmus.backend.movie.batch;

import com.filmus.backend.movie.dto.NlpKeywordResponseDTO;
import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.external.NlpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieItemProcessor implements ItemProcessor<Movie, Movie> {

    private final NlpClient nlpClient;

    @Override
    public Movie process(Movie movie) {
        if (movie.getPlot() == null || movie.getPlot().isBlank()) return null;

        try {
            var res = nlpClient.extractKeywords(movie.getPlot());
            if (res == null || res.extracted_keywords() == null) return null;

            String joined = res.extracted_keywords().stream()
                    .map(k -> k.keyword().trim())
                    .collect(Collectors.joining(","));
            movie.setPlotKeywords(joined);
            return movie;

        } catch (Exception e) {          // ❗️모든 오류 로깅 후 스킵
            log.warn("⚠️ NLP 호출 실패 movieId={}: {}", movie.getId(), e.toString());
            return null;                 // null 반환 → 해당 아이템 skip
        }
    }
}