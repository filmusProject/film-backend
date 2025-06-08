// File: com/filmus/backend/movie/sync/MovieSyncService.java
package com.filmus.backend.movie.sync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.es.MovieDocument;
import com.filmus.backend.movie.external.NlpClient;
import com.filmus.backend.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MovieSyncService {

    private final WebClient webClient;
    private final MovieRepository movieRepository;
    private final NlpClient nlpClient;
    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper;
    private final PageStateRepository pageStateRepo;

    @Value("${kmdb.service-key}")
    private String serviceKey;

    private static final String INDEX     = "movies";
    private static final int    BATCH_SIZE = 1_000;

    /**
     * KMDb → DB → NLP → ES 동기화
     * PageState.lastPage 를 읽어서, 그다음 페이지부터 계속 실행
     */
    @Transactional
    public void syncAll() throws IOException {
        // 1) 이전 동기화 페이지 읽기 (없으면 id=1,lastPage=0)
        PageState state = pageStateRepo.findById(1)
                .orElseGet(() -> PageState.builder().id(1).lastPage(0).build());
        int page       = state.getLastPage();
        int totalCount;

        do {
            int startCount = page * BATCH_SIZE;

            // 2) KMDb API 호출
            String resp = webClient.get()
                    .uri(b -> b.path("/openapi-data2/wisenut/search_api/search_json2.jsp")
                            .queryParam("ServiceKey", serviceKey)
                            .queryParam("listCount", BATCH_SIZE)
                            .queryParam("startCount", startCount)
                            .queryParam("collection", "kmdb_new2")
                            .queryParam("detail", "Y")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root    = objectMapper.readTree(resp);
            JsonNode data    = root.path("Data").get(0);
            totalCount       = data.path("TotalCount").asInt();
            JsonNode results = data.path("Result");

            // 3) 각 결과 처리
            for (JsonNode node : results) {
                String docid = node.path("DOCID").asText();
                if (movieRepository.existsByDocid(docid)) continue;

                // 3-1) DB 저장
                Movie m = Movie.builder()
                        .docid(docid)
                        .movieId(node.path("movieId").asText())
                        .movieSeq(node.path("movieSeq").asText())
                        .title(node.path("title").asText())
                        .plot(node.path("plots").path("plot").get(0).path("plotText").asText(""))
                        // …필요한 다른 필드도 동일하게…
                        .build();
                movieRepository.save(m);

                // 3-2) NLP → plotKeywords 업데이트
                if (m.getPlot() != null && !m.getPlot().isBlank()) {
                    var kws = nlpClient.extractKeywords(m.getPlot()).extracted_keywords();
                    String joined = kws.stream()
                            .map(k -> k.keyword())
                            .distinct()
                            .reduce((a,b)-> a+","+b)
                            .orElse("");
                    m.setPlotKeywords(joined);
                    movieRepository.save(m);
                }

                // 3-3) ES 색인
                MovieDocument doc = MovieDocument.fromEntity(m);
                IndexRequest req = new IndexRequest(INDEX)
                        .id(doc.getDocid())
                        .source(objectMapper.convertValue(doc, Map.class));
                esClient.index(req, RequestOptions.DEFAULT);
            }

            // 4) 다음 페이지로 이동 & 상태 저장
            page++;
            state.setLastPage(page);
            pageStateRepo.save(state);

        } while (page * BATCH_SIZE < totalCount);
    }
}