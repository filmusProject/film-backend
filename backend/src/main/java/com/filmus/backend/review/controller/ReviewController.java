package com.filmus.backend.review.controller;

import com.filmus.backend.review.dto.ReviewRequestDTO;
import com.filmus.backend.review.dto.ReviewResponseDTO;
import com.filmus.backend.review.service.ReviewService;
import com.filmus.backend.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "리뷰 API", description = "영화 한줄평(리뷰) 작성, 수정, 삭제, 조회 기능 제공")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "로그인한 사용자가 영화에 대한 한줄평과 별점을 작성합니다.")
    @PostMapping
    public Long createReview(@RequestBody @Valid ReviewRequestDTO requestDTO,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        return reviewService.createReview(requestDTO, userId);
    }

    @Operation(summary = "리뷰 수정", description = "로그인한 사용자가 본인이 작성한 리뷰를 수정하거나, 관리자가 리뷰를 수정할 수 있습니다.")
    @PutMapping("/{reviewId}")
    public void updateReview(@PathVariable Long reviewId,
                             @RequestBody @Valid ReviewRequestDTO requestDTO,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        boolean isAdmin = userDetails.isAdmin();
        reviewService.updateReview(reviewId, requestDTO, userId, isAdmin);
    }

    @Operation(summary = "리뷰 삭제", description = "로그인한 사용자가 본인이 작성한 리뷰를 삭제하거나, 관리자가 리뷰를 삭제할 수 있습니다.")
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUserId();
        boolean isAdmin = userDetails.isAdmin();
        reviewService.deleteReview(reviewId, userId, isAdmin);
    }

    @Operation(summary = "영화별 리뷰 조회", description = "특정 영화에 대한 모든 리뷰를 조회합니다.")
    @GetMapping("/movie/{movieId}")
    public List<ReviewResponseDTO> getReviewsByMovie(@PathVariable Long movieId) {
        return reviewService.getReviewsByMovie(movieId);
    }
}
