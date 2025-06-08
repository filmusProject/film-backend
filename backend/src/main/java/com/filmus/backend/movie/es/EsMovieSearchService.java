package com.filmus.backend.movie.es;


import com.filmus.backend.movie.dto.SearchRequestDTO;
import com.filmus.backend.movie.dto.SearchResponseDTO;
import com.filmus.backend.movie.es.MovieSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EsMovieSearchService {
    private final MovieSearchRepository movieSearchRepository;

    public SearchResponseDTO search(SearchRequestDTO request) throws IOException {
        return movieSearchRepository.searchConditional(request);
    }
}