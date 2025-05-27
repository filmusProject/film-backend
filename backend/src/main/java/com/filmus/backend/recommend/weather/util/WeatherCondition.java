package com.filmus.backend.recommend.weather.util;

/**
 * OpenWeatherMap의 날씨 상태(main)에 대응하는 Enum
 */
public enum WeatherCondition {
    CLEAR("Clear"),
    CLOUDS("Clouds"),
    RAIN("Rain"),
    SNOW("Snow"),
    THUNDERSTORM("Thunderstorm"),
    DRIZZLE("Drizzle"),
    MIST("Mist"),
    OTHER("Other");

    private final String value;

    WeatherCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 문자열 상태값 → Enum 매핑
     */
    public static WeatherCondition from(String value) {
        for (WeatherCondition condition : values()) {
            if (condition.value.equalsIgnoreCase(value)) {
                return condition;
            }
        }
        return OTHER;
    }
}
