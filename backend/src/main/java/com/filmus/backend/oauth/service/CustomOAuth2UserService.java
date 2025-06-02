package com.filmus.backend.oauth.service;

import com.filmus.backend.user.entity.User;
import com.filmus.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 소셜 로그인(OAuth2) 성공 후 사용자 정보를 받아오고,
 * DB에 해당 사용자가 없다면 자동 회원가입을 처리하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;            // 사용자 저장소
    private final PasswordEncoder passwordEncoder;          // 비밀번호 암호화 도구

    /**
     * OAuth2 로그인 성공 후 사용자 정보를 불러오는 메서드
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 부모 클래스(DefaultOAuth2UserService)를 통해 기본 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("🔍 [OAuth2 DEBUG] oAuth2User.getAttributes() = {}", oAuth2User.getAttributes());

        // 어떤 소셜 플랫폼에서 로그인했는지 확인 (예: "kakao")
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        log.info("[OAuth2] provider = {}", provider);
        log.info("[OAuth2] attributes = {}", oAuth2User.getAttributes());

        // 카카오의 사용자 정보를 담고 있는 attributes Map
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 고유 ID는 최상단에 위치함 (Long 타입 → String 변환)
        String providerId = String.valueOf(attributes.get("id"));

        // 닉네임은 properties 내부에 위치함
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (properties != null) ? (String) properties.get("nickname") : "소셜사용자";

        // 이메일은 kakao_account 내부에 위치함
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (kakaoAccount != null) ? (String) kakaoAccount.get("email") : null;

        // provider + providerId로 기존 사용자 조회
        Optional<User> optionalUser = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;

        if (optionalUser.isPresent()) {
            // 이미 가입된 사용자라면 그대로 사용
            user = optionalUser.get();
            log.info("[OAuth2] 기존 회원 로그인: provider={}, providerId={}", provider, providerId);
        } else {
            // 신규 사용자면 회원가입 자동 처리
            // 임시 비밀번호 생성 및 암호화
            String dummyPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            // 사용자 객체 생성 및 저장
            user = User.createSocialUser(email, nickname, provider, providerId, dummyPassword);
            userRepository.save(user);
            log.info("[OAuth2] 신규 회원 자동 가입: provider={}, providerId={}", provider, providerId);
        }



        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                "id"
        );

    }
}
