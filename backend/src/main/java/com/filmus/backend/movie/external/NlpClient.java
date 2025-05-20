package com.filmus.backend.movie.external;

import com.filmus.backend.movie.dto.NlpKeywordRequestDTO;
import com.filmus.backend.movie.dto.NlpKeywordResponseDTO;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Component
public class NlpClient {

    @Value("${nlp.baseUrl}")
    private String baseUrl;

    private WebClient webClient;

    public NlpKeywordResponseDTO extractKeywords(String description) {

        if (webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(new ReactorClientHttpConnector(
                            HttpClient.create(ConnectionProvider.builder("nlpPool")
                                            .maxConnections(30)
                                            .pendingAcquireTimeout(Duration.ofSeconds(30))
                                            .build())
                                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                                    .responseTimeout(Duration.ofSeconds(30))
                    ))
                    .build();
        }

        return webClient.post()
                .uri("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new NlpKeywordRequestDTO(description))
                .retrieve()
                .bodyToMono(NlpKeywordResponseDTO.class)
                .block();
    }
}