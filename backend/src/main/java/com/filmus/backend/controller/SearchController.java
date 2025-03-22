package com.filmus.backend.controller;

import com.filmus.backend.dto.SearchRequest;
import com.filmus.backend.dto.SearchResponseDTO;
import com.filmus.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Mono<SearchResponseDTO> search(SearchRequest request) {
        return searchService.search(request);
    }
}