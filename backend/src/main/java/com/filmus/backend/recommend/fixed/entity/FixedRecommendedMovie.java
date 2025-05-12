package com.filmus.backend.recommend.fixed.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedRecommendedMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String movieId; // KMDB 영화 ID
    private String movieSeq; // KMDB 영화 일련번호
    private String title; // 영화 제목
    private String prodyear; // 제작 연도
    private String genre; // 장르 문자열 (예: 액션,드라마)
    private String posterUrl; // 포스터 이미지 URL
    private LocalDate recommendedDate; // 추천 날짜
}
