// File: com/filmus/backend/movie/sync/MovieSyncScheduler.java
package com.filmus.backend.movie.sync;

import com.filmus.backend.movie.service.MovieService;
import com.filmus.backend.movie.external.NlpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieSyncScheduler {
    private final MovieSyncService syncService;
    private final WebClient webClient;        // from WebClientConfig, for KMDb
    private final NlpClient nlpClient;        // for NLP
    private final MovieService movieService;  // to reuse buildUri if needed

    @Value("${kmdb.service-key}")
    private String serviceKey;

    /**
     * 매일 새벽 3시 실행:
     * 1) KMDb API(첫 페이지) 헬스체크
     * 2) NLP 서버 헬스체크 (extractKeywords 테스트 호출)
     * 둘 다 OK일 때만 syncService.syncAll() 실행
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void runDailySync() {
        log.info("[Scheduler] 동기화 시작 전 헬스체크 중...");

        boolean kmdbUp = checkKmdb();
        boolean nlpUp  = checkNlp();

        if (!kmdbUp) {
            log.warn("[Scheduler] KMDb API 연결 실패. 오늘은 동기화를 건너뜁니다.");
            return;
        }
        if (!nlpUp) {
            log.warn("[Scheduler] NLP 서버 연결 실패. 오늘은 동기화를 건너뜁니다.");
            return;
        }

        log.info("[Scheduler] 헬스체크 통과. MovieSyncService.syncAll() 실행");
        try {
            syncService.syncAll();
            log.info("[Scheduler] 동기화 완료");
        } catch (Exception e) {
            log.error("[Scheduler] 동기화 중 오류 발생", e);
        }
    }

    /**
     * KMDb API 헬스체크:
     * 첫 페이지 URL에 HEAD 요청을 보내보고 2xx 응답 여부 확인
     */
    private boolean checkKmdb() {
        try {
            Mono<ClientResponse> respMono = webClient
                    .head()
                    .uri(uriBuilder ->
                            movieService.buildUri(uriBuilder, /* dummy DTO */ null, 1, 0)
                    )
                    .exchangeToMono(Mono::just);
            return respMono.block(Duration.ofSeconds(5))
                    .statusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("[Scheduler] KMDb 헬스체크 예외", e);
            return false;
        }
    }

    /**
     * NLP 서버 헬스체크:
     * 빈 문자열로 키워드 추출 시도를 해보고, 예외가 없으면 OK
     */
    private boolean checkNlp() {
        try {
            // 빈 플롯도 정상적으로 처리되면 up 으로 간주
            nlpClient.extractKeywords("");
            return true;
        } catch (Exception e) {
            log.warn("[Scheduler] NLP 헬스체크 예외", e);
            return false;
        }
    }
}