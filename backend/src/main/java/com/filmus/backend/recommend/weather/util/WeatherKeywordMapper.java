package com.filmus.backend.recommend.weather.util;

import java.util.List;
import java.util.Map;

/**
 * 날씨 상태에 따른 추천 키워드 리스트를 반환하는 유틸 클래스
 */
public class WeatherKeywordMapper {

    // 날씨 상태 → 키워드 매핑 (JSON 기반 사전에서 발췌 + 보완)
    private static final Map<WeatherCondition, List<String>> WEATHER_KEYWORDS = Map.of(
            WeatherCondition.CLEAR, List.of("가족", "희망", "여행", "휴식", "성장", "청춘", "따뜻함", "햇살"),
            WeatherCondition.CLOUDS, List.of("우울", "혼란", "몽환", "불확실성", "내면", "분열"),
            WeatherCondition.RAIN, List.of("음모", "복수", "긴장", "느와르", "추리", "심리", "고립"),
            WeatherCondition.SNOW, List.of("감성", "사랑", "겨울", "추억", "동화", "회상", "청춘"),
            WeatherCondition.THUNDERSTORM, List.of("재난", "공포", "스릴러", "위기", "폭력", "절망"),
            WeatherCondition.DRIZZLE, List.of("외로움", "로맨스", "이별", "사색", "그리움"),
            WeatherCondition.MIST, List.of("미스터리", "의문", "의심", "수수께끼", "정체불명")
    );

    /**
     * 날씨 상태에 따라 추천 키워드 리스트를 반환
     * @param weatherMain 날씨 상태 문자열 (ex. "Clear")
     * @return 키워드 리스트 (일치 없으면 빈 리스트)
     */
    public static List<String> getKeywords(String weatherMain) {
        WeatherCondition condition = WeatherCondition.from(weatherMain);
        return WEATHER_KEYWORDS.getOrDefault(condition, List.of());
    }
}
