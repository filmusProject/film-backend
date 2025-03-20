package com.filmus.backend.entity;

import jakarta.persistence.*;

// 데이터베이스의 users 테이블과 연결되는 User 엔티티
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 사용자 고유 ID (자동 증가)

    @Column(nullable = false, unique = true)
    private String username; // 사용자 아이디 (유니크)

    @Column(nullable = false)
    private String password; // 사용자 비밀번호

    // 기본 생성자
    public User() {}

    // 모든 필드를 초기화하는 생성자
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter와 Setter
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
