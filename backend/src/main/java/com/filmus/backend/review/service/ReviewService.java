package com.filmus.backend.review.service;

import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.review.dto.ReviewRequestDTO;
import com.filmus.backend.review.dto.ReviewResponseDTO;
import com.filmus.backend.review.entity.Review;
import com.filmus.backend.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 작성
     */
    public Long createReview(ReviewRequestDTO requestDTO, Long userId) {
        Review review = Review.builder()
                .content(requestDTO.getContent())
                .rating(requestDTO.getRating())
                .userId(userId)
                .movieId(requestDTO.getMovieId())
                .build();

        Review savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

    /**
     * 리뷰 수정
     */
    public void updateReview(Long reviewId, ReviewRequestDTO requestDTO, Long userId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUserId().equals(userId) && !isAdmin) {
            throw new CustomException(ErrorCode.REVIEW_NO_PERMISSION);
        }

        review.updateContentAndRating(requestDTO.getContent(), requestDTO.getRating());
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long reviewId, Long userId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUserId().equals(userId) && !isAdmin) {
            throw new CustomException(ErrorCode.REVIEW_NO_PERMISSION);
        }

        reviewRepository.delete(review);
    }

    /**
     * 영화별 리뷰 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByMovie(Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        return reviews.stream()
                .map(review -> ReviewResponseDTO.builder()
                        .reviewId(review.getId())
                        .rating((float) review.getRating())
                        .content(review.getContent())
                        .userId(review.getUserId())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}