package com.filmus.backend.recommend.award.entity;

import com.filmus.backend.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "award_recommended_movies")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardRecommendedMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String movieId; // KMDB 영화 ID
    private String movieSeq; // KMDB 영화 일련번호
    private String title; // 영화 제목
    private String year; // 제작 연도
    private String genre; // 장르 문자열 (예: 액션,드라마)
    private String posterUrl; // 포스터 이미지 URL
    private LocalDate recommendedDate; // 추천 날짜


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType festival; // 영화제 종류 (칸, 베니스, 부산)

    @Column(nullable = false)
    private int entryOFyear; // 출품 연도 (언제 영화제 후보에 올라갔는가)

//    @Column(nullable = false)
//    private LocalDateTime createdAt; // 입력일시

//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now(); // 자동 생성
//    }

    // festival 타입 정의 (Enum)
    public enum FestivalType {
        CANNES,
        VENICE,
        BUSAN
    }
}
