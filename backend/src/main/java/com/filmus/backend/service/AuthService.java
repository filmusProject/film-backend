package com.filmus.backend.service; // 서비스 클래스가 포함될 패키지 경로

import com.filmus.backend.entity.User; // User 엔티티 클래스 임포트 (DB의 users 테이블과 매핑됨)
import com.filmus.backend.repository.UserRepository; // 사용자 정보를 조회하기 위한 JPA Repository 임포트
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 비밀번호 암호화 및 검증을 위한 BCryptPasswordEncoder 임포트
import org.springframework.stereotype.Service; // Spring의 서비스 컴포넌트임을 나타내는 @Service 어노테이션 임포트

import java.util.Optional; // null 값을 처리하기 위한 Optional 클래스 임포트

// AuthService 클래스: 로그인 검증 로직을 담당하는 서비스 클래스
@Service // Spring의 서비스 계층으로 등록하여 의존성 주입 가능하도록 설정
public class AuthService {

    private final UserRepository userRepository; // 데이터베이스에서 사용자 정보를 조회할 UserRepository 인터페이스
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증을 담당할 BCryptPasswordEncoder

    // AuthService 생성자: UserRepository를 주입받아 사용자 데이터 조회 기능을 제공
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository; // UserRepository 객체를 생성자 주입을 통해 초기화
        this.passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호를 암호화하고 검증하기 위한 BCryptPasswordEncoder 인스턴스 생성
    }

    //  validateUser(): 사용자의 아이디와 비밀번호가 데이터베이스와 일치하는지 검증하는 메서드
    public boolean validateUser(String username, String password) {
        //  데이터베이스에서 username에 해당하는 사용자 정보를 조회
        Optional<User> userOptional = userRepository.findByUsername(username);

        //  사용자가 존재하고, 입력한 비밀번호가 DB에 저장된 암호화된 비밀번호와 일치하는지 검증
        return userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword());
        //  userOptional.isPresent(): 사용자 정보가 존재하는지 확인
        //  passwordEncoder.matches(): 입력된 비밀번호를 암호화된 값과 비교 (비밀번호 일치 여부 확인)
    }
}
