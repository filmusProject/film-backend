package com.filmus.backend.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {

    @NotNull(message = "별점은 필수입니다.")
    @DecimalMin(value = "0.5", inclusive = true, message = "별점은 최소 0.5 이상이어야 합니다.")
    @DecimalMax(value = "5.0", inclusive = true, message = "별점은 최대 5.0 이하여야 합니다.")
    private Float rating;

    @Size(max = 255, message = "한줄평은 255자 이내여야 합니다.")
    private String content;

    @NotNull(message = "영화 ID는 필수입니다.")
    private Long movieId;
}