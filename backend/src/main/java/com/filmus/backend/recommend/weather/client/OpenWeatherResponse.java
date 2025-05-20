package com.filmus.backend.recommend.weather.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * OpenWeatherMap의 현재 날씨 API (/data/2.5/weather) 응답을 매핑하는 DTO
 */
@Getter
@NoArgsConstructor
public class OpenWeatherResponse {

    // "weather" 배열: 날씨 상태 정보 (예: Clear, Rain)
    private List<Weather> weather;

    /**
     * weather[0]의 main 필드를 반환 (예: "Clear", "Clouds")
     */
    public String getCurrentWeatherMain() {
        if (weather != null && !weather.isEmpty()) {
            return weather.get(0).getMain();
        } else {
            return "Clear";  // 기본값 fallback
        }
    }

    /**
     * 내부 클래스: weather[0] 객체의 구조 매핑
     */
    @Getter
    @NoArgsConstructor
    public static class Weather {
        private String main;  // 날씨 상태 (예: "Clear", "Rain")
    }
}
