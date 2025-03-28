package com.filmus.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.dto.MovieDTO;
import com.filmus.backend.dto.SearchRequestDTO;
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
public class MovieService {
    // 필드
    private final WebClient webClient; // HTTP 요청을 전송하기 위한 WebClient
    @Value("${kmdb.service-key}")
    private String serviceKey; // 외부 KMDB API 호출 시 필요한 서비스 키
    private final ObjectMapper mapper = new ObjectMapper(); // JSON 파싱에 사용하는 Jackson 라이브러리의 ObjectMapper

    // 1. 영화 검색 메서드 (search)
    // 클라이언트에서 전달받은 SearchRequestDTO(검색 조건)에 맞는 영화 검색 결과를 KMDB API 호출을 통해 반환
    public Mono<SearchResponseDTO> search(SearchRequestDTO req) {
        int listCount = 50; // 한 번의 API 요청에서 가져올 아이템 수
        int startCount = (req.page() != null && req.page() > 0) ? (req.page() - 1) * listCount : 0; // 페이지 처리

        return webClient.get()
                .uri(uriBuilder -> buildUri(uriBuilder, req, listCount, startCount)) // URI 생성 빌드
                .retrieve() // API 호출
                .bodyToMono(String.class) // 응답 데이터를 JSON 형태의 String으로 읽음
                .map(this::parseResponse); // 응답 데이터를 필요한 형태로 변환
    }

    // 2. 영화 상세 정보 반환 메서드 (detail)
    public Mono<MovieDTO> detail(String movieId, String movieSeq) {
        int listCount = 1; // 상세 정보는 한 개만 반환
        int startCount = 0;

        // SearchRequestDTO를 기본값들과 함께 영화 ID와 순번 값을 포함하여 생성
        SearchRequestDTO detailSearch = new SearchRequestDTO(
                null, null, null, null, null, null, null, null,
                null, null, movieId, movieSeq, null, null, null,
                null, null, null, null);

        return webClient.get()
                .uri(uriBuilder -> buildUri(uriBuilder, detailSearch, listCount, startCount)) // API 호출 URI 빌드
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseResponse_Detail); // JSON 응답 데이터 파싱
    }

    // 3. URI 생성 메서드 (KMDB API 요청 URI 구성)
    private URI buildUri(UriBuilder builder, SearchRequestDTO req, int listCount, int startCount) {
        builder.path("/openapi-data2/wisenut/search_api/search_json2.jsp")
                .queryParam("ServiceKey", serviceKey) // 필수 인증 키
                .queryParam("listCount", listCount) // 요청당 아이템 수
                .queryParam("startCount", startCount) // 시작 인덱스
                .queryParam("collection", "kmdb_new2") // 데이터 컬렉션 정보
                .queryParam("detail", "Y") // 상세 정보 포함 요청
                .queryParam("query", req.query() == null ? "" : req.query());

        // SearchRequestDTO의 조건별로 필요한 매개변수를 URI에 추가
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

    // 4. 검색 응답 데이터 파싱 메서드
    private SearchResponseDTO parseResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json); // JSON 문자열을 Jackson JsonNode로 변환
            JsonNode dataNode = root.path("Data").get(0); // "Data" 배열의 첫 번째 객체
            JsonNode resultArray = dataNode.path("Result"); // "Result" 속성 배열

            // 최종 결과를 저장할 MovieSimpleDTO 리스트
            List<SearchResponseDTO.MovieSimpleDTO> movies = new ArrayList<>();
            for (JsonNode node : resultArray) {
                String[] posterList = node.path("posters").asText().split("\\|");
                String posterUrl = (posterList.length > 0) ? posterList[0] : ""; // 첫 번째 포스터 URL 가져옴

                movies.add(new SearchResponseDTO.MovieSimpleDTO(
                        node.path("movieId").asText(),
                        node.path("movieSeq").asText(),
                        node.path("title").asText(),
                        node.path("prodYear").asText(),
                        posterUrl)); // 영화 간단 정보 추가
            }

            int totalCount = dataNode.path("TotalCount").asInt(); // 전체 결과 수
            return new SearchResponseDTO(totalCount, movies); // SearchResponseDTO로 반환
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 실패", e);
        }
    }

    // 5. 상세 정보 응답 데이터 파싱 메서드
    private MovieDTO parseResponse_Detail(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode dataNode = root.path("Data").get(0);
            JsonNode resultArray = dataNode.path("Result");

            if (resultArray.isEmpty()) {
                throw new RuntimeException("ResultArray에 데이터가 없습니다.");
            }

            MovieDTO movie = null;
            for (JsonNode node : resultArray) {
                // 배우 정보 추출
                JsonNode actorArray = node.path("actors").path("actor");
                String actorNm = extractNames(actorArray, "actorNm");

                // 감독 정보 추출
                JsonNode directorArray = node.path("directors").path("director");
                String directorNm = extractNames(directorArray, "directorNm");

                // 줄거리 정보 추출
                JsonNode plotArray = node.path("plots").path("plot");
                String plot = (plotArray.isArray() && plotArray.size() > 0)
                        ? plotArray.get(0).path("plotText").asText()
                        : "";

                // VOD 정보 추가 로직은 구현 필요
                // ...
            }
            return movie;
        } catch (Exception e) {
            throw new RuntimeException("상세 응답 파싱 실패", e);
        }
    }

    private String extractNames(JsonNode arrayNode, String attribute) {
        if (!arrayNode.isArray() || arrayNode.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        arrayNode.forEach(node -> {
            String name = node.path(attribute).asText();
            if (!name.isBlank()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(name);
            }
        });
        return sb.toString();
    }
}