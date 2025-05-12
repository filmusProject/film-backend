package com.filmus.backend.movie.controller;

import com.filmus.backend.movie.dto.*;
import com.filmus.backend.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@Tag(name = "영화 API", description = "KMDb 영화 검색 및 상세 정보 조회 API")
@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final JobLauncher jobLauncher;
    private final Job nlpKeywordJob;

    @Operation(
            summary = "영화 검색 API",
            description = "사용자가 입력한 영화 제목, 감독, 배우 등의 키워드를 기반으로 KMDb OpenAPI를 통해 영화를 검색하고 결과에 대해 간단한 영화ID, 영화 제목, 영화 개봉년도, 영화 포스터 주소 정보로 이루어진 리스트를 반환합니다.."
    )
    @GetMapping(value = "/search")
    public SearchResponseDTO search(SearchRequestDTO request) {
        return movieService.search(request);
    }


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

    @Operation(
            summary = "NLP 키워드 배치 실행",
            description = "전체 영화 데이터를 대상으로 plot 필드에 기반해 NLP 분석을 수행하고, 결과를 plot_keywords 컬럼에 저장합니다."
    )
    @PostMapping("/nlp/batch")
    public ResponseEntity<String> runNlpKeywordBatch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(nlpKeywordJob, params);
            return ResponseEntity.ok("Batch job started: " + execution.getStatus());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Batch job failed: " + e.getMessage());
        }
    }
}