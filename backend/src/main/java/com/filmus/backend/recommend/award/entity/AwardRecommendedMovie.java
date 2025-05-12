package com.filmus.backend.recommend.award.entity;

import com.filmus.backend.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "award_recommended_movies")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AwardRecommendedMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie; // 영화 테이블과의 FK 관계

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalType festival; // 영화제 종류 (칸, 베니스, 부산)

    @Column(nullable = false)
    private int year; // 출품 연도 (언제 영화제 후보에 올라갔는가)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 입력일시

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // 자동 생성
    }

    // festival 타입 정의 (Enum)
    public enum FestivalType {
        CANNES,
        VENICE,
        BUSAN
    }
}
