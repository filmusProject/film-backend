package com.filmus.backend.oauth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * OAuth2 로그인 응답에서 필요한 사용자 정보를 추출하고,
 * 내부 도메인에 맞는 형태로 통일하여 전달하는 DTO 클래스입니다.
 *
 * 소셜 로그인 제공자마다 사용자 응답 구조가 다르기 때문에,
 * 이 클래스는 제공자별 응답을 일관된 구조(email, nickname, provider 등)로 변환합니다.
 */
@Getter
public class OAuthAttributes {

    private final String email;       // 사용자 이메일 (nullable)
    private final String nickname;    // 사용자 닉네임
    private final String provider;    // 소셜 제공자 이름 (ex: "KAKAO")
    private final String providerId;  // 제공자별 사용자 고유 ID

    @Builder
    public OAuthAttributes(String email, String nickname, String provider, String providerId) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
    }

    /**
     * 소셜 제공자(provider)에 따라 사용자 정보를 파싱하여 OAuthAttributes 객체로 변환합니다.
     */
    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(provider)) {
            return ofKakao(attributes);
        }
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인 제공자: " + provider);
    }

    /**
     * 카카오 로그인 응답에서 사용자 정보를 추출합니다.
     * 현재 이메일은 받을 수 없는 상태이므로 nickname과 id 중심으로 처리합니다.
     */
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        // 사용자 고유 식별 ID (필수)
        String providerId = String.valueOf(attributes.get("id"));

        // 닉네임은 properties 내부에 존재
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (properties != null) ? (String) properties.get("nickname") : null;

        // 현재 email은 받을 수 없으므로 null 처리
        return OAuthAttributes.builder()
                .email(null)
                .nickname(nickname)
                .provider("KAKAO")  // 대문자로 통일
                .providerId(providerId)
                .build();
    }
}
