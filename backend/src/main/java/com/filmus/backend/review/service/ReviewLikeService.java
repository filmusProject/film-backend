package com.filmus.backend.review.service;

import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.review.entity.Review;
import com.filmus.backend.review.entity.ReviewLike;
import com.filmus.backend.review.repository.ReviewLikeRepository;
import com.filmus.backend.review.repository.ReviewRepository;
import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public void likeReview(Long userId, Long reviewId) {
        if (reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LIKE);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ReviewLike like = ReviewLike.builder()
                .user(user)
                .review(review)
                .build();

        reviewLikeRepository.save(like);
    }

    public void unlikeReview(Long userId, Long reviewId) {
        if (!reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        reviewLikeRepository.deleteByUserIdAndReviewId(userId, reviewId);
    }

    public Long countLikes(Long reviewId) {
        return reviewLikeRepository.countByReviewId(reviewId);
    }
}