package com.filmus.backend.repository;

import com.filmus.backend.entity.RefreshToken;
import com.filmus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RefreshToken 엔티티를 위한 JPA 리포지토리 인터페이스입니다.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 사용자 기반으로 리프레시 토큰 조회
    Optional<RefreshToken> findByUser(User user);

    // 사용자 기반으로 리프레시 토큰 삭제
    void deleteByUser(User user);
}