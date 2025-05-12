package com.filmus.backend.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String docid;

    @Column
    private String movieId;

    @Column
    private String movieSeq;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String titleEtc;

    @Column(length = 1000)
    private String directorNm;

    @Column(columnDefinition = "TEXT")
    private String actorNm;

    @Column
    private String nation;

    @Column
    private String company;

    @Column
    private String prodYear;

    @Column(columnDefinition = "TEXT")
    private String plot;

    @Column
    private String runtime;

    @Column
    private String rating;

    @Column(length = 500)
    private String genre;

    @Column
    private String kmdbUrl;

    @Column
    private String type;

    @Column
    private String isUse;

    @Column
    private String ratedYn;

    @Column
    private String repRatDate;

    @Column
    private String repRlsDate;

    @Column(columnDefinition = "TEXT") // 변경: TEXT로 확장
    private String keywords;

    @Column(length = 2000) // 변경: 길이 증가
    private String posterUrl;

    @Column(columnDefinition = "TEXT") // 이미 충분히 처리되어 있음
    private String stillUrl;

    @Column
    private String vodClass;

    @Column(length = 2000) // 변경: 길이 증가
    private String vodUrl;

    @Column(columnDefinition = "TEXT") // 변경: TEXT로 확장
    private String awards1;

    @Column(columnDefinition = "TEXT") // 수상내역도 충분히 길 수 있음
    private String awards2;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String plotKeywords;
}