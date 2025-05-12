package com.filmus.backend.movie.repository;

import com.filmus.backend.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    // 영화의 docid로 영화 존재 여부 확인
    Optional<Movie> findByDocid(String docid);

    // movieId와 movieSeq로 영화를 찾음
    Optional<Movie> findByMovieIdAndMovieSeq(String movieId, String movieSeq);

    /** 아직 키워드가 없는 영화만 */
    @Query("""
           select m
           from Movie m
           where m.plot is not null
             and (m.plotKeywords is null or m.plotKeywords = '')
           """)
    List<Movie> findForKeywordExtraction();

//    @Query("""
//           select m
//           from Movie m
//           where m.plotKeywords is not null
//             and size(
//                   (select elements from Product(p)  )
//                 ) > 0
//           """)
//    List<Movie> findByKeywordIntersection(Set<String> keywords);
}