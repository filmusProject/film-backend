package com.filmus.backend.recommend.fixed.repository;

import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovieQueryRepositoryImpl implements MovieQueryRepository {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> findMoviesByGenreKeyword(String keyword) {
        // "%액션%" 검색처럼 작동
        return movieRepository.findByGenreContaining(keyword);
    }
}
