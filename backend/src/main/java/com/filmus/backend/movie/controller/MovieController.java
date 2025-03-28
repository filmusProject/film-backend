package com.filmus.backend.movie.controller;

import com.filmus.backend.movie.dto.MovieDTO;
import com.filmus.backend.movie.dto.SearchRequestDTO;
import com.filmus.backend.movie.dto.SearchResponseDTO;
import com.filmus.backend.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(
            summary = "영화 검색 API",
            description = "사용자가 입력한 영화 제목, 감독, 배우 등의 키워드를 기반으로 KMDb OpenAPI를 통해 영화를 검색하고 결과에 대해 간단한 영화ID, 영화 제목, 영화 개봉년도, 영화 포스터 주소 정보로 이루어진 리스트를 반환합니다.."
    )
    @GetMapping(value = "/search")
    public Mono<SearchResponseDTO> search(SearchRequestDTO request) {
        return movieService.search(request);
    }


    @Operation(
            summary = "영화 상세 정보 조회 (KMDb OpenAPI)",
            description = "영화 ID와 영화 SEQ를 이용해 KMDb OpenAPI에서 특정 영화의 상세 정보를 조회합니다."
    )
    @GetMapping(value = "/detail")
    public Mono<MovieDTO> detail(@RequestParam String movieId, @RequestParam String movieSeq) {return movieService.detail(movieId,movieSeq);}
}