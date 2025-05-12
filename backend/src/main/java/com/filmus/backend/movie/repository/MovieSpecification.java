package com.filmus.backend.movie.repository;

import com.filmus.backend.movie.dto.SearchRequestDTO;
import com.filmus.backend.movie.entity.Movie;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate; 

public class MovieSpecification {

    private static final DateTimeFormatter D8 = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static Specification<Movie> of(SearchRequestDTO req) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            /* ---------- 1) 자유 키워드 검색 ---------- */
            if (StringUtils.hasText(req.query())) {
                String q = "%" + req.query().trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"),       q),
                        cb.like(root.get("directorNm"),  q),
                        cb.like(root.get("actorNm"),     q),
                        cb.like(root.get("keywords"),    q),
                        cb.like(root.get("plot"),        q)
                ));
            }

            /* ---------- 2) 날짜(연도) 범위 ---------- */
            // createDts / createDte : prodYear 기준 (yyyy)
            if (StringUtils.hasText(req.createDts()) || StringUtils.hasText(req.createDte())) {
                int from = req.createDts() != null ? Integer.parseInt(req.createDts().substring(0, 4)) : 0;
                int to   = req.createDte()  != null ? Integer.parseInt(req.createDte().substring(0, 4))  : 9999;
                predicates.add(cb.between(root.get("prodYear"), String.valueOf(from), String.valueOf(to)));
            }

            // releaseDts / releaseDte : repRlsDate(yyyyMMdd) 기준
            if (StringUtils.hasText(req.releaseDts()) || StringUtils.hasText(req.releaseDte())) {
                LocalDate from = req.releaseDts() != null ? LocalDate.parse(req.releaseDts(), D8) : LocalDate.of(0001,1,1);
                LocalDate to   = req.releaseDte()  != null ? LocalDate.parse(req.releaseDte(),  D8) : LocalDate.of(9999,12,31);
                predicates.add(cb.between(root.get("repRlsDate"), from.format(D8), to.format(D8)));
            }

            /* ---------- 3) 단일 필터들 ---------- */
            eq(predicates, cb, root.get("nation"),     req.nation());
            eq(predicates, cb, root.get("company"),    req.company());
            like(predicates, cb, root.get("genre"),    req.genre());   // 포함 검색
            eq(predicates, cb, root.get("isUse"),      req.use());
            eq(predicates, cb, root.get("movieId"),    req.movieId());
            eq(predicates, cb, root.get("movieSeq"),   req.movieSeq());
            eq(predicates, cb, root.get("type"),       req.type());
            like(predicates, cb, root.get("title"),    req.title());
            like(predicates, cb, root.get("directorNm"), req.director());
            like(predicates, cb, root.get("actorNm"),  req.actor());
            like(predicates, cb, root.get("keywords"), req.keyword());
            like(predicates, cb, root.get("plot"),     req.plot());

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /* ---------- helpers ---------- */
    private static void eq(List<Predicate> list, jakarta.persistence.criteria.CriteriaBuilder cb,
                           jakarta.persistence.criteria.Path<String> path, String value) {
        if (StringUtils.hasText(value)) list.add(cb.equal(path, value.trim()));
    }
    private static void like(List<Predicate> list, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (StringUtils.hasText(value)) list.add(cb.like(path, "%" + value.trim() + "%"));
    }

    public static Specification<Movie> plotKeywordsContainsAny(List<String> keywords) {
        return (root, query, cb) -> {
            List<Predicate> ors = new ArrayList<>();
            for (String kw : keywords) {
                ors.add(cb.like(root.get("plotKeywords"), "%" + kw + "%"));
            }
            return cb.or(ors.toArray(new Predicate[0]));
        };
    }

}