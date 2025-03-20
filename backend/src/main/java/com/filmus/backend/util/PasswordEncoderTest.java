package com.filmus.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 추후에 지워도 되는 코드임
public class PasswordEncoderTest {

    public static void main(String[] args) {
        // 비밀번호 암호화를 수행할 BCryptPasswordEncoder 객체 생성
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // "testpassword"를 암호화하여 출력
        String encodedPassword = encoder.encode("testpassword");

        // 암호화된 비밀번호 출력 (이 값을 DB에 저장해야 함)
        System.out.println("Encoded Password: " + encodedPassword);
    }
}
