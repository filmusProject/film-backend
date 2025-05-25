package com.filmus.backend.security;

import com.filmus.backend.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user; // 실제 도메인 엔티티

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 스프링 시큐리티가 요구하는 기본 메서드들 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: 단일권한 ROLE_USER 만 준다거나, DB에서 꺼내온다거나
        return Collections.singletonList(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 로그인 id로 email을 쓸 수도 있고 username을 쓸 수도 있음
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(user.getRole());
    }

    public Long getUserId(){
        return user.getId();
    }
}