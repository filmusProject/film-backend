package com.filmus.backend.movie.controller;

import com.filmus.backend.movie.dto.*;
import com.filmus.backend.movie.es.EsMovieSearchService;
import com.filmus.backend.movie.service.MovieService;
import com.filmus.backend.movie.sync.EsMovieIndexLoader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Tag(name = "영화 API", description = "KMDb 영화 검색 및 상세 정보 조회 API")
@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final EsMovieSearchService esMovieSearchService;
    private final EsMovieIndexLoader indexLoader;

    @Operation(
            summary = "영화 검색 API",
            description = "검색 엔진 선택(sql 또는 es)과 키워드 및 필터를 기반으로 영화 목록을 반환합니다."
    )
    @GetMapping("/search")
    public SearchResponseDTO search(
            @ModelAttribute SearchRequestDTO request,
            @RequestParam(value = "engine", defaultValue = "sql") String engine
    ) throws IOException {
        if ("es".equalsIgnoreCase(engine)) {
            // Elasticsearch/OpenSearch 검색
            return esMovieSearchService.search(request);
        } else {
            // 기본 SQL(JPA) 검색
            return movieService.search(request);
        }
    }
//    @Operation(
//            summary = "영화 검색 API",
//            description = "사용자가 입력한 영화 제목, 감독, 배우 등의 키워드를 기반으로 KMDb OpenAPI를 통해 영화를 검색하고 결과에 대해 간단한 영화ID, 영화 제목, 영화 개봉년도, 영화 포스터 주소 정보로 이루어진 리스트를 반환합니다.."
//    )
//    @GetMapping(value = "/search")
//    public SearchResponseDTO search(SearchRequestDTO request) {
//        return movieService.search(request);
//    }


    @Operation(
            summary = "영화 상세 정보 조회 (KMDb OpenAPI)",
            description = "movieID와 영화 movieSeq를 이용해 KMDb OpenAPI에서 특정 영화의 상세 정보를 조회합니다."
    )
    @GetMapping(value = "/detail")
    public MovieDTO detail(@RequestParam String movieId, @RequestParam String movieSeq) {return movieService.detail(movieId,movieSeq);}

    /** 🔍 줄거리 설명으로 유사 영화 검색 */
    @Operation(
            summary = "줄거리 기반 유사 영화 검색",
            description = "입력된 줄거리(description)를 기반으로 NLP 분석 후 유사한 키워드를 가진 영화를 반환합니다."
    )
    @PostMapping("/nlp/search")
    public ResponseEntity<PlotSearchResponseDTO> searchPlot(@RequestBody NlpKeywordRequestDTO request) {
        var result = movieService.searchByPlotDescription(request.description(), 30);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reindex")
    public ResponseEntity<String> reindexMovies() {
        try {
            indexLoader.reindexAll();
            return ResponseEntity.ok("Reindex job completed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Reindex failed: " + e.getMessage());
        }
    }
}

