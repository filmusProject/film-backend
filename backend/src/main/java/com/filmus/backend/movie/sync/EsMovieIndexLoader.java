// 수정된 File: com/filmus/backend/movie/service/EsMovieIndexLoader.java
package com.filmus.backend.movie.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.es.MovieDocument;
import com.filmus.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class EsMovieIndexLoader {

    private final MovieRepository movieRepository;
    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper;
    private final PageStateRepository stateRepo;

    private static final String INDEX = "movies";
    private static final int PAGE_SIZE = 1000;

    /**
     * SQL DB에서 마지막 처리 페이지부터 이어서 ES에 Bulk 색인.
     */
    @Transactional
    public void reindexAll() throws IOException {
        PageState state = stateRepo.findById(2)
                .orElse(new PageState(2, 0));
        int page = state.getLastPage();
        Page<Movie> moviePage;

        do {
            moviePage = movieRepository.findAll(PageRequest.of(page, PAGE_SIZE));
            BulkRequest bulk = new BulkRequest();

            for (Movie m : moviePage.getContent()) {
                MovieDocument doc = MovieDocument.fromEntity(m);
                IndexRequest req = new IndexRequest(INDEX)
                        .id(doc.getDocid())
                        .source(objectMapper.convertValue(doc, Map.class));
                bulk.add(req);
            }

            if (bulk.numberOfActions() > 0) {
                // ① bulk 실행 후 응답 객체를 받아서
                BulkResponse resp = esClient.bulk(bulk, RequestOptions.DEFAULT);

                // ② 실패가 있는지 체크
                if (resp.hasFailures()) {
                    log.error("===== Bulk indexing failures on page {} =====", page);
                    for (BulkItemResponse item : resp.getItems()) {
                        if (item.isFailed()) {
                            log.error("Failed to index [{}]: {}",
                                    item.getId(),
                                    item.getFailureMessage());
                        }
                    }
                } else {
                    log.info("Bulk page {} indexed successfully ({} items)", page, resp.getItems().length);
                }
            }

            page++;
            state.setLastPage(page);
            stateRepo.save(state);

        } while (moviePage.hasNext());
    }
}