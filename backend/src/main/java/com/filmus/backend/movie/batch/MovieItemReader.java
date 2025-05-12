package com.filmus.backend.movie.batch;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieItemReader {

    private final MovieRepository movieRepository;

    @Bean
    public IteratorItemReader<Movie> movieReader() {
        return new IteratorItemReader<>(movieRepository.findForKeywordExtraction());
    }
}