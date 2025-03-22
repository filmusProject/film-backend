package com.filmus.backend.controller;

import com.filmus.backend.dto.SearchRequest;
import com.filmus.backend.dto.SearchResponseDTO;
import com.filmus.backend.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(
            summary = "영화 검색 (KMDb OpenAPI)",
            description = "영화 제목, 감독, 배우 등의 정보를 기반으로 KMDb OpenAPI에서 영화를 검색합니다."
    )
    @GetMapping
    public Mono<SearchResponseDTO> search(SearchRequest request) {
        return searchService.search(request);
    }
}