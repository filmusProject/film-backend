package com.filmus.backend.recommend.weather.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * OpenWeatherMap의 현재 날씨 API(/data/2.5/weather)를 호출하여
 * 현재 날씨 상태(main)를 조회하는 클라이언트
 */
@Component
@Slf4j
public class WeatherClient {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    private static final double SEOUL_LAT = 37.5665;
    private static final double SEOUL_LON = 126.9780;
    /**
     * 위도와 경도를 기반으로 현재 날씨 상태(main)를 조회한다.
     *

     * @return 날씨 상태(main) 문자열 (예: Clear, Rain)
     */
    public String getCurrentWeatherMainByLatLon() {
        try {
            String uri = String.format(
                    "%s?lat=%f&lon=%f&units=metric&appid=%s",
                    apiUrl, SEOUL_LAT, SEOUL_LON, apiKey
            );

            log.info("[날씨 API 호출] {}", uri);

            OpenWeatherResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(OpenWeatherResponse.class)
                    .block();

            return response.getWeather().get(0).getMain();  // weather[0].main
        } catch (Exception e) {
            log.error("날씨 API 호출 실패: {}", e.getMessage());
            return "Clear";  // 실패 시 fallback
        }
    }
}
