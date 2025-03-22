package com.filmus.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.dto.MovieDTO;
import com.filmus.backend.dto.SearchRequest;
import com.filmus.backend.dto.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final WebClient webClient;

    @Value("${kmdb.service-key}")
    private String serviceKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public Mono<SearchResponseDTO> search(SearchRequest req){
        int listCount = 10;
        int startCount = (req.page() != null && req.page() > 0) ? (req.page() - 1) * listCount : 0;

        return webClient.get()
                .uri(uriBuilder -> buildUri(uriBuilder, req, listCount, startCount))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseResponse);
    }
    private java.net.URI buildUri(UriBuilder builder, SearchRequest req, int listCount, int startCount) {
        builder.path("/openapi-data2/wisenut/search_api/search_json2.jsp")
                .queryParam("ServiceKey", serviceKey)
                .queryParam("listCount", listCount)
                .queryParam("startCount", startCount)
                .queryParam("collection", "kmdb_new2")
                .queryParam("detail", "Y")
                .queryParam("query", req.query() == null ? "" : req.query());


        if (req.createDts() != null) builder.queryParam("createDts", req.createDts());
        if (req.createDte() != null) builder.queryParam("createDte", req.createDte());
        if (req.releaseDts() != null) builder.queryParam("releaseDts", req.releaseDts());
        if (req.releaseDte() != null) builder.queryParam("releaseDte", req.releaseDte());
        if (req.nation() != null) builder.queryParam("nation", req.nation());
        if (req.company() != null) builder.queryParam("company", req.company());
        if (req.genre() != null) builder.queryParam("genre", req.genre());
        if (req.use() != null) builder.queryParam("use", req.use());
        if (req.movieId() != null) builder.queryParam("movieId", req.movieId());
        if (req.movieSeq() != null) builder.queryParam("movieSeq", req.movieSeq());
        if (req.type() != null) builder.queryParam("type", req.type());
        if (req.title() != null) builder.queryParam("title", req.title());
        if (req.director() != null) builder.queryParam("director", req.director());
        if (req.actor() != null) builder.queryParam("actor", req.actor());
        if (req.staff() != null) builder.queryParam("staff", req.staff());
        if (req.keyword() != null) builder.queryParam("keyword", req.keyword());
        if (req.plot() != null) builder.queryParam("plot", req.plot());

        URI uri = builder.build();
        System.out.println("✅ 최종 요청 URI: " + uri);
        return uri;
    }
    private SearchResponseDTO parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode dataNode = root.path("Data").get(0);
            JsonNode resultArray = dataNode.path("Result");

            List<MovieDTO> movies = new ArrayList<>();

            for (JsonNode node : resultArray) {

                // ✅ 배우명 전체 추출
                JsonNode actorArray = node.path("actors").path("actor");
                String actorNm = "";
                if (actorArray.isArray()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < actorArray.size(); i++) {
                        String name = actorArray.get(i).path("actorNm").asText();
                        if (!name.isBlank()) {
                            if (sb.length() > 0) sb.append(", ");
                            sb.append(name);
                        }
                    }
                    actorNm = sb.toString();
                }

                // ✅ (선택) 감독명도 동일한 방식으로 추출해도 됨
                JsonNode directorArray = node.path("directors").path("director");
                String directorNm = "";
                if (directorArray.isArray()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < directorArray.size(); i++) {
                        String name = directorArray.get(i).path("directorNm").asText();
                        if (!name.isBlank()) {
                            if (sb.length() > 0) sb.append(", ");
                            sb.append(name);
                        }
                    }
                    directorNm = sb.toString();
                }

                // ✅ 줄거리도 마찬가지 (plot 여러 개면)
                JsonNode plotArray = node.path("plots").path("plot");
                String plot = "";
                if (plotArray.isArray() && plotArray.size() > 0) {
                    plot = plotArray.get(0).path("plotText").asText(); // 첫 번째 줄거리만 사용
                }

                // ✅ VOD 정보
                JsonNode vodArray = node.path("vods").path("vod");
                String vodClass = "";
                String vodUrl = "";
                if (vodArray.isArray() && vodArray.size() > 0) {
                    vodClass = vodArray.get(0).path("vodClass").asText();
                    vodUrl = vodArray.get(0).path("vodUrl").asText();
                }

                // MovieDTO에 최종 전달
                movies.add(new MovieDTO(
                        node.path("DOCID").asText(),
                        node.path("movieId").asText(),
                        node.path("movieSeq").asText(),
                        node.path("title").asText(),
                        node.path("titleEtc").asText(),
                        directorNm,
                        actorNm,
                        node.path("nation").asText(),
                        node.path("company").asText(),
                        node.path("prodYear").asText(),
                        plot,
                        node.path("runtime").asText(),
                        node.path("rating").asText(),
                        node.path("genre").asText(),
                        node.path("kmdbUrl").asText(),
                        node.path("type").asText(),
                        node.path("use").asText(),
                        node.path("ratedYn").asText(),
                        node.path("repRatDate").asText(),
                        node.path("repRlsDate").asText(),
                        node.path("keywords").asText(),
                        node.path("posters").asText(),
                        node.path("stlls").asText(),
                        vodClass,
                        vodUrl,
                        node.path("Awards1").asText()
                ));
            }
            int totalCount = dataNode.path("TotalCount").asInt();

            return new SearchResponseDTO(totalCount, movies);

        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패", e);
        }
    }

}
