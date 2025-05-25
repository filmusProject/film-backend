package com.filmus.backend.review.controller;

import com.filmus.backend.review.dto.ReviewLikeDTO;
import com.filmus.backend.review.service.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews/likes")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @Operation(
            summary = "리뷰 좋아요 등록",
            description = "사용자가 특정 리뷰에 좋아요를 등록합니다. 중복 등록은 허용되지 않습니다."
    )
    @PostMapping
    public ResponseEntity<Void> like(@RequestBody ReviewLikeDTO dto) {
        reviewLikeService.likeReview(dto.userId(), dto.reviewId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "리뷰 좋아요 취소",
            description = "사용자가 누른 특정 리뷰의 좋아요를 취소합니다."
    )
    @DeleteMapping
    public ResponseEntity<Void> unlike(@RequestBody ReviewLikeDTO dto) {
        reviewLikeService.unlikeReview(dto.userId(), dto.reviewId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "리뷰 좋아요 수 조회",
            description = "특정 리뷰에 등록된 좋아요 수를 반환합니다."
    )
    @GetMapping("/count")
    public ResponseEntity<Long> count(@RequestParam Long reviewId) {
        return ResponseEntity.ok(reviewLikeService.countLikes(reviewId));
    }
}