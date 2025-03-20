package com.filmus.backend.repository;

import com.filmus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// User 엔티티와 연결되는 JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 사용자명(username)으로 사용자 찾기
}
