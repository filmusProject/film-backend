// movie/external/NlpClient.java
package com.filmus.backend.movie.external;

import com.filmus.backend.movie.dto.*;
import com.filmus.backend.movie.util.DebounceScheduler;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.resources.ConnectionProvider;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class NlpClient {

    private final NlpEc2Manager ec2Manager;
    private final DebounceScheduler debounce;

    @Value("${nlp.baseUrl}")
    private String baseUrl;

    private WebClient webClient;              // 지연 생성

    public NlpKeywordResponseDTO extractKeywords(String description) {

        /* 1) EC2 기동 보장 */
        ec2Manager.ensureRunning();

        /* 2) WebClient 없는 경우 생성 (Elastic IP 기반이라 상관없음) */
        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(new ReactorClientHttpConnector(
                            HttpClient.create(ConnectionProvider.builder("nlpPool")
                                            .maxConnections(30)           // 동시 30커넥션
                                            .pendingAcquireTimeout(Duration.ofSeconds(30))
                                            .build())
                                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                                    .responseTimeout(Duration.ofSeconds(30))   // ⬅ 30 초로 ↑
                    ))
                    .build();
        }

        NlpKeywordResponseDTO dto =
                webClient.post()
                        .uri("/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new NlpKeywordRequestDTO(description))
                        .retrieve()
                        .bodyToMono(NlpKeywordResponseDTO.class)
                        .block();

        /* 4) idle 타이머 초기화 */
        debounce.markUsed();
        return dto;
    }
}