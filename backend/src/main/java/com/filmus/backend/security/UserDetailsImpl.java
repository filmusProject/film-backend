package com.filmus.backend.security;

import com.filmus.backend.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 인증된 사용자 정보를 담는 UserDetails 구현체
 * Spring Security는 인증된 사용자를 내부적으로 UserDetails 라는 인터페이스로 다뤄.
 * → 그런데 너는 User라는 엔티티를 쓰고 있잖아?
 *
 * 그래서 UserDetailsImpl을 만들어서, User 객체를 감싸고
 * → Spring Security가 인식할 수 있게 해주는 거야.
 */
@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 없다면 빈 리스트 반환
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 실제 암호화된 비밀번호
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // username 필드 기준
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료 안됨)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠긴 계정 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }
}