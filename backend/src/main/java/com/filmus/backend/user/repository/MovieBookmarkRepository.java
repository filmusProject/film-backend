package com.filmus.backend.user.repository;

import com.filmus.backend.user.entity.MovieBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieBookmarkRepository extends JpaRepository<MovieBookmark, Long> {

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    List<MovieBookmark> findAllByUserId(Long userId);
}