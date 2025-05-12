// movie/external/NlpClient.java
package com.filmus.backend.movie.external;

import com.filmus.backend.movie.dto.*;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class NlpClient {

    /** ❶ 커넥션 풀 + 넉넉한 타임아웃 + 재시도 */
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://nlp:5001")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create(ConnectionProvider.builder("nlpPool")
                                    .maxConnections(30)           // 동시 30커넥션
                                    .pendingAcquireTimeout(Duration.ofSeconds(30))
                                    .build())
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                            .responseTimeout(Duration.ofSeconds(30))   // ⬅ 30 초로 ↑
            ))
            .build();

    public NlpKeywordResponseDTO extractKeywords(String description) {
        return webClient.post()
                .uri("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new NlpKeywordRequestDTO(description))
                .retrieve()
                .bodyToMono(NlpKeywordResponseDTO.class)
                .timeout(Duration.ofSeconds(30))              // ⬅ Mono 타임아웃도 30 초
                .retryWhen(Retry.backoff(2, Duration.ofMillis(500))
                        .filter(t -> t instanceof TimeoutException))
                .block();
    }
}