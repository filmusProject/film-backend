package com.filmus.backend.review.repository;

import com.filmus.backend.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 영화에 대한 리뷰 목록 조회
    List<Review> findByMovieId(Long movieId);

    // 특정 유저가 쓴 특정 리뷰 찾기 (수정, 삭제할 때 본인 검증용)
    Review findByIdAndUserId(Long id, Long userId);
}