// File: com/filmus/backend/movie/es/MovieSearchRepository.java
package com.filmus.backend.movie.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.movie.dto.SearchRequestDTO;
import com.filmus.backend.movie.dto.SearchResponseDTO;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import static org.opensearch.index.query.QueryBuilders.functionScoreQuery;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MovieSearchRepository {
    private final RestHighLevelClient client;
    private final ScoreFunctionFactory scoreFactory;
    private final ObjectMapper objectMapper;
    private static final String INDEX = "movies";

    public SearchResponseDTO searchConditional(SearchRequestDTO req) throws IOException {
        int page = req.page() != null ? req.page() - 1 : 0;
        int size = 20;

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (req.query() != null && !req.query().isBlank()) {
            boolQuery.must(QueryBuilders.multiMatchQuery(
                    req.query(),
                    "title", "directorNm", "actorNm", "keywords"
            ));
        }
        if (req.createDts() != null && req.createDte() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("prodYear")
                    .gte(req.createDts())
                    .lte(req.createDte())
            );
        }
        if (req.releaseDts() != null && req.releaseDte() != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("repRlsDate")
                    .gte(req.releaseDts())
                    .lte(req.releaseDte())
            );
        }
        if (req.nation() != null)   boolQuery.filter(QueryBuilders.termQuery("nation",   req.nation()));
        if (req.company() != null)  boolQuery.filter(QueryBuilders.termQuery("company",  req.company()));
        if (req.genre()   != null)  boolQuery.filter(QueryBuilders.termQuery("genre",    req.genre()));
        if (req.use()     != null)  boolQuery.filter(QueryBuilders.termQuery("isUse",    req.use()));
        if (req.movieId() != null)  boolQuery.filter(QueryBuilders.termQuery("movieId",  req.movieId()));
        if (req.movieSeq()!= null)  boolQuery.filter(QueryBuilders.termQuery("movieSeq", req.movieSeq()));
        if (req.type()    != null)  boolQuery.filter(QueryBuilders.termQuery("type",     req.type()));
        if (req.title()   != null)  boolQuery.filter(QueryBuilders.matchQuery("title",     req.title()));
        if (req.director()!= null)  boolQuery.filter(QueryBuilders.matchQuery("directorNm",req.director()));
        if (req.actor()   != null)  boolQuery.filter(QueryBuilders.matchQuery("actorNm",   req.actor()));
        if (req.keyword() != null)  boolQuery.filter(QueryBuilders.matchQuery("keywords",  req.keyword()));
        if (req.plot()    != null)  boolQuery.filter(QueryBuilders.matchQuery("plot",      req.plot()));

        // functionScoreQuery 호출 (기본 score_mode는 'sum' 이므로 별도 설정 불필요)
        FunctionScoreQueryBuilder fsqb = functionScoreQuery(
                boolQuery,
                scoreFactory.buildCommonScoreFuncs()
        );

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(fsqb)
                .from(page * size)
                .size(size)
                .sort("_score", SortOrder.DESC);

        SearchResponse resp = client.search(
                new SearchRequest(INDEX).source(source),
                RequestOptions.DEFAULT
        );

        List<SearchResponseDTO.MovieSimpleDTO> movies = Arrays.stream(resp.getHits().getHits())
                .map(hit -> objectMapper.convertValue(hit.getSourceAsMap(), MovieDocument.class))
                .map(doc -> new SearchResponseDTO.MovieSimpleDTO(
                        doc.getMovieId(),
                        doc.getMovieSeq(),
                        doc.getTitle(),
                        doc.getProdYear(),
                        doc.getPosterUrl()
                ))
                .collect(Collectors.toList());

        return new SearchResponseDTO(
                (int) resp.getHits().getTotalHits().value,
                movies
        );
    }
}