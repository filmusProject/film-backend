package com.filmus.backend.movie.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filmus.backend.common.exception.CustomException;
import com.filmus.backend.common.exception.ErrorCode;
import com.filmus.backend.movie.dto.*;
import com.filmus.backend.movie.entity.Movie;
import com.filmus.backend.movie.external.NlpClient;
import com.filmus.backend.movie.repository.MovieRepository;
import com.filmus.backend.movie.repository.MovieSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {

    private static final int PAGE_SIZE = 30;
    private final WebClient webClient;
    private final MovieRepository movieRepository;
    private final NlpClient nlpClient;

    @Value("${kmdb.service-key}")
    private String serviceKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * KMDb 영화 검색 요청 처리
     */
    public SearchResponseDTO search(SearchRequestDTO req) {
        try {
            int pageIndex = (req.page() != null && req.page() > 0) ? req.page() - 1 : 0;

            PageRequest pageRequest =
                    PageRequest.of(pageIndex, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "prodYear", "id"));

            Page<Movie> page = movieRepository.findAll(
                    MovieSpecification.of(req), pageRequest);

            return new SearchResponseDTO(
                    (int) page.getTotalElements(),
                    page.getContent().stream()
                            .map(this::toSimpleDTO)
                            .collect(Collectors.toList())
            );

        } catch (Exception e) {
            // 로그를 남기고 세부 에러를 래핑
            throw new CustomException(ErrorCode.KMDB_SEARCH_FAILED);
        }

//        레거시 코드 kmdb openAPI를 통해 가져오는 코드
//        try {
//            String response = webClient.get()
//                    .uri(uriBuilder -> buildUri(uriBuilder, req, 50,
//                            (req.page() != null && req.page() > 0) ? (req.page() - 1) * 50 : 0))
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .blockOptional()
//                    .orElseThrow(() -> new CustomException(ErrorCode.KMDB_NO_RESPONSE));
//
//            return processSearchResponse(response);
//        } catch (Exception e) {
//            throw new CustomException(ErrorCode.KMDB_SEARCH_FAILED);
//        }
    }

    /* ---------- Entity → DTO 변환 ---------- */
    private SearchResponseDTO.MovieSimpleDTO toSimpleDTO(Movie m) {
        return new SearchResponseDTO.MovieSimpleDTO(
                m.getMovieId(),
                m.getMovieSeq(),
                m.getTitle(),
                m.getProdYear(),
                m.getPosterUrl()
        );
    }

    /**
     * KMDb 영화 상세 요청 처리
     */
    public MovieDTO detail(String movieId, String movieSeq) {
        Optional<Movie> optionalMovie = movieRepository.findByMovieIdAndMovieSeq(movieId, movieSeq);
        if (optionalMovie.isPresent()) {
            return convertToDTO(optionalMovie.get());
        }

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> buildUriForDetail(uriBuilder, movieId, movieSeq))
                    .retrieve()
                    .bodyToMono(String.class)
                    .blockOptional()
                    .orElseThrow(() -> new CustomException(ErrorCode.KMDB_NO_RESPONSE));

            return processDetailResponse(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MOVIE_DETAIL_FAILED);
        }
    }

    public PlotSearchResponseDTO searchByPlotDescription(String description, int limit) {

        /* 1) NLP 서버 호출 */
        var nlpRes = nlpClient.extractKeywords(description);

        /* 2) 키워드 리스트 (중복 제거) */
        List<String> keywords = nlpRes.extracted_keywords().stream()
                .map(NlpKeywordResponseDTO.ExtractedKeyword::keyword)
                .map(String::trim)
                .distinct()
                .toList();

        if (keywords.isEmpty()) {
            return new PlotSearchResponseDTO(nlpRes.extracted_keywords(), nlpRes.summary(), List.of());
        }

        /* 3) 후보 영화 조회 */
        List<Movie> candidates = movieRepository.findAll(
                MovieSpecification.plotKeywordsContainsAny(keywords)
        );

        /* 4) 유사도 계산 및 정렬 */
        List<MovieMatchDTO> matches = candidates.stream()
                .map(m -> {
                    Set<String> movieKw = Arrays.stream(m.getPlotKeywords().split("\\s*,\\s*"))
                            .collect(Collectors.toSet());

                    List<String> common = keywords.stream()
                            .filter(movieKw::contains)
                            .toList();

                    return new MovieMatchDTO(
                            m.getId(),
                            m.getMovieSeq(),
                            m.getTitle(),
                            m.getProdYear(),
                            m.getPosterUrl(),
                            common
                    );
                })
                .filter(mm -> !mm.matchedKeywords().isEmpty())
                .sorted(Comparator.comparingInt((MovieMatchDTO mm) -> mm.matchedKeywords().size()).reversed())
                .limit(limit)
                .toList();

        return new PlotSearchResponseDTO(nlpRes.extracted_keywords(), nlpRes.summary(), matches);
    }

    /**
     * 검색 URI 생성
     */
    private URI buildUri(UriBuilder builder, SearchRequestDTO req, int listCount, int startCount) {
        builder.path("/openapi-data2/wisenut/search_api/search_json2.jsp")
                .queryParam("ServiceKey", serviceKey)
                .queryParam("listCount", listCount)
                .queryParam("startCount", startCount)
                .queryParam("collection", "kmdb_new2")
                .queryParam("detail", "Y")
                .queryParam("query", req.query() != null ? req.query() : "");

        // 요청 파라미터 추가
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
        log.debug("✅ 최종 요청 URI: {}", uri);
        return uri;
    }

    /**
     * 상세 조회용 URI 생성
     */
    private URI buildUriForDetail(UriBuilder builder, String movieId, String movieSeq) {
        return builder.path("/openapi-data2/wisenut/search_api/search_json2.jsp")
                .queryParam("ServiceKey", serviceKey)
                .queryParam("listCount", 1)
                .queryParam("startCount", 0)
                .queryParam("collection", "kmdb_new2")
                .queryParam("detail", "Y")
                .queryParam("movieId", movieId)
                .queryParam("movieSeq", movieSeq)
                .build();
    }

    /**
     * 검색 응답 처리
     */
    private SearchResponseDTO processSearchResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode dataNode = root.path("Data").get(0);
            JsonNode resultArray = dataNode.path("Result");

            List<SearchResponseDTO.MovieSimpleDTO> movies = new ArrayList<>();
            for (JsonNode node : resultArray) {
                String movieId = node.path("movieId").asText();
                String movieSeq = node.path("movieSeq").asText();
                saveOrUpdate(node); // 없으면 추가, 있으면 갱신

                String[] posterList = node.path("posters").asText().split("\\|");
                String posterUrl = (posterList.length > 0) ? posterList[0] : "";

                movies.add(new SearchResponseDTO.MovieSimpleDTO(
                        movieId,
                        movieSeq,
                        cleanTags(node.path("title").asText()),
                        node.path("prodYear").asText(),
                        posterUrl
                ));
            }

            int totalCount = dataNode.path("TotalCount").asInt();
            return new SearchResponseDTO(totalCount, movies);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.KMDB_PARSE_FAILED);
        }
    }

    /**
     * 상세 응답 처리
     */
    private MovieDTO processDetailResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode dataNode = root.path("Data").get(0);
            JsonNode resultArray = dataNode.path("Result");

            if (resultArray.isEmpty()) {
                throw new CustomException(ErrorCode.MOVIE_DETAIL_NOT_FOUND);
            }

            JsonNode node = resultArray.get(0);
            Movie movie = saveOrUpdate(node); // 저장 또는 갱신
            return convertToDTO(movie);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.KMDB_PARSE_FAILED);
        }
    }

    /**
     * 영화 정보 저장 또는 갱신
     */
    private Movie saveOrUpdate(JsonNode node) {
        String movieId = node.path("movieId").asText();
        String movieSeq = node.path("movieSeq").asText();

        String actorNm = extractNames(node.path("actors").path("actor"), "actorNm");
        String directorNm = extractNames(node.path("directors").path("director"), "directorNm");
        String keywords = removeHighlightTags(node.path("keywords").asText());
        String plot = node.path("plots").path("plot").size() > 0
                ? node.path("plots").path("plot").get(0).path("plotText").asText()
                : "";

        String[] posterList = node.path("posters").asText().split("\\|");
        String posterUrl = (posterList.length > 0) ? posterList[0] : "";

        // 기존 DB에 존재하면 가져오고, 없으면 새로 생성
        Movie movie = movieRepository.findByMovieIdAndMovieSeq(movieId, movieSeq)
                .orElse(Movie.builder()
                        .movieId(movieId)
                        .movieSeq(movieSeq)
                        .build());

        // 모든 필드 업데이트
        movie.setDocid(node.path("DOCID").asText());
        movie.setTitle(removeHighlightTags(node.path("title").asText()));
        movie.setTitleEtc(node.path("titleEtc").asText());
        movie.setDirectorNm(directorNm);
        movie.setActorNm(actorNm);
        movie.setNation(node.path("nation").asText());
        movie.setCompany(node.path("company").asText());
        movie.setProdYear(node.path("prodYear").asText());
        movie.setPlot(plot);
        movie.setRuntime(node.path("runtime").asText());
        movie.setRating(node.path("rating").asText());
        movie.setGenre(node.path("genre").asText());
        movie.setKmdbUrl(node.path("kmdbUrl").asText());
        movie.setType(node.path("type").asText());
        movie.setIsUse(node.path("use").asText());
        movie.setRatedYn(node.path("ratedYn").asText());
        movie.setRepRatDate(node.path("repRatDate").asText());
        movie.setRepRlsDate(node.path("repRlsDate").asText());
        movie.setKeywords(keywords);
        movie.setPosterUrl(posterUrl);
        movie.setStillUrl(node.path("stlls").asText());
        movie.setVodClass(node.path("vodClass").asText());
        movie.setVodUrl(node.path("vodUrl").asText());
        movie.setAwards1(node.path("Awards1").asText());
        movie.setPlotKeywords(node.path("plotKeywords").asText());

        return movieRepository.save(movie);
    }

    /**
     * Entity → DTO 변환
     */
    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getDocid(),
                movie.getMovieId(),
                movie.getMovieSeq(),
                movie.getTitle(),
                movie.getTitleEtc(),
                movie.getDirectorNm(),
                movie.getActorNm(),
                movie.getNation(),
                movie.getCompany(),
                movie.getProdYear(),
                movie.getPlot(),
                movie.getRuntime(),
                movie.getRating(),
                movie.getGenre(),
                movie.getKmdbUrl(),
                movie.getType(),
                movie.getIsUse(),
                movie.getRatedYn(),
                movie.getRepRatDate(),
                movie.getRepRlsDate(),
                movie.getKeywords(),
                movie.getPosterUrl(),
                movie.getStillUrl(),
                movie.getVodClass(),
                movie.getVodUrl(),
                movie.getAwards1(),
                movie.getPlotKeywords()
        );
    }

    /**
     * 감독 또는 배우 이름 추출 및 정제
     */
    private String extractNames(JsonNode arrayNode, String attribute) {
        if (!arrayNode.isArray() || arrayNode.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        arrayNode.forEach(node -> {
            String name = removeHighlightTags(node.path(attribute).asText());
            if (!name.isBlank()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(name);
            }
        });
        return sb.toString();
    }

    /**
     * !HS, !HE 태그 제거 및 공백 정리
     */
    private String cleanTags(String title) {
        if (title == null) return null;
        String cleaned = title.replace("!HS", "").replace("!HE", "");
        return cleaned.replaceAll("\\s+", " ").trim();
    }

    /**
     * 모든 필드에서 !HS, !HE 태그 제거
     */
    private String removeHighlightTags(String input) {
        if (input == null) return null;
        return input.replace("!HS", "").replace("!HE", "").replaceAll("\\s+", " ").trim();
    }
}