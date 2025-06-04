package com.filmus.backend.oauth.service;

import com.filmus.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 카카오 소셜 연동 해제를 처리하는 서비스 클래스입니다.
 * 사용자가 회원 탈퇴 시, 카카오 계정과의 연결을 해제(unlink)하기 위해 사용됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUnlinkService {

    // 카카오 플랫폼의 REST API admin key
    // (개발자센터 > 앱 설정 > 앱 키 > REST API Admin 키)
    @Value("${kakao.admin-key}")
    private String adminKey;

    /**
     * 주어진 사용자가 카카오 계정 연동 사용자라면,
     * 카카오 unlink API를 호출하여 연결을 해제합니다.
     *
     * @param user 소셜 로그인된 사용자 객체 (provider, providerId 필수)
     * @return 연동 해제 성공 여부 (true = 성공, false = 실패 또는 provider 불일치)
     */
    public boolean unlink(User user) {
        // 카카오 로그인 사용자만 처리 가능
        if (!"KAKAO".equalsIgnoreCase(user.getProvider())) {
            return false;
        }

        // 카카오 사용자 연동 해제 API 엔드포인트
        String url = "https://kapi.kakao.com/v1/user/unlink";

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 폼 데이터 형식
        headers.set("Authorization", "KakaoAK " + adminKey); // 카카오 admin key로 인증

        // 요청 바디 설정 (target_id_type: user_id, target_id: 카카오 회원번호)
        Map<String, String> body = new HashMap<>();
        body.put("target_id_type", "user_id");
        body.put("target_id", user.getProviderId()); // providerId는 카카오에서 발급된 유저 ID

        // 요청 엔티티 생성 (헤더 + 바디)
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // POST 요청 전송
            ResponseEntity<String> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // HTTP 200 OK 응답이면 성공 처리
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            // 예외 발생 시 로그 기록 후 false 반환
            log.warn("카카오 연동 해제 실패: {}", e.getMessage());
            return false;
        }
    }
}
