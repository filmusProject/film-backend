package com.filmus.backend.movie.repository;

import com.filmus.backend.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 영화의 docid로 영화 존재 여부 확인
    Optional<Movie> findByDocid(String docid);

    // movieId와 movieSeq로 영화를 찾음
    Optional<Movie> findByMovieIdAndMovieSeq(String movieId, String movieSeq);

    // MovieRepository.java
    List<Movie> findByGenreContaining(String genre);

}