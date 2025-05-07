package com.filmus.backend.user.service;

import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.repository.MovieRepository;
import com.filmus.backend.user.dto.BookmarkResponseDTO;
import com.filmus.backend.user.entity.MovieBookmark;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.MovieBookmarkRepository;
import com.filmus.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBookmarkService {

    private final MovieBookmarkRepository bookmarkRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public void addBookmark(Long userId, Long movieId) {
        if (bookmarkRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new CustomException(ErrorCode.DUPLICATE_BOOKMARK);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        bookmarkRepository.save(MovieBookmark.builder()
                .user(user)
                .movie(movie)
                .build());
    }

    @Transactional
    public void removeBookmark(Long userId, Long movieId) {
        if (!bookmarkRepository.existsByUserIdAndMovieId(userId, movieId)) {
            throw new CustomException(ErrorCode.BOOKMARK_NOT_FOUND);
        }

        bookmarkRepository.deleteByUserIdAndMovieId(userId, movieId);
    }


    public boolean isBookmarked(Long userId, Long movieId) {
        return bookmarkRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    public List<BookmarkResponseDTO> getBookmarks(Long userId) {
        List<MovieBookmark> bookmarks = bookmarkRepository.findAllByUserId(userId);
        return bookmarks.stream()
                .map(b -> new BookmarkResponseDTO(
                        b.getMovie().getId(),
                        b.getMovie().getTitle(),
                        b.getMovie().getPosterUrl()
                ))
                .toList();
    }
}