package com.filmus.backend.movie.batch;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MovieItemWriter implements ItemWriter<Movie> {

    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends Movie> chunk) {
        List<Movie> save = (List<Movie>) chunk.getItems().stream()
                .filter(Objects::nonNull)
                .toList();
        movieRepository.saveAll(save);
    }
}