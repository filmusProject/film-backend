package com.filmus.backend.user.repository;

import com.filmus.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// User 엔티티와 연결된 JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // username으로 사용자 조회 (로그인 시 사용)
    Optional<User> findByUsername(String username);

    // email로 사용자 조회 (이메일 중복 체크 or 찾기 기능에 사용)
    Optional<User> findByEmail(String email);

    //
    Optional<User> findByProviderAndProviderId(String provider, String providerId); // ✅ 추가

    // username 중복 여부 확인 (true/false)
    boolean existsByUsername(String username);

    // email 중복 여부 확인 (true/false)
    boolean existsByEmail(String email);
}
