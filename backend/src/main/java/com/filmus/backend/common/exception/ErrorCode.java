package com.filmus.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 🔐 인증/인가 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "유효하지 않은 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_005", "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "AUTH_006", "이메일 인증이 필요합니다."),

    // 메일 전송 관련
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_001", "이메일 전송에 실패했습니다."),

    // 👤 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER_004", "이미 사용 중인 아이디입니다."),

    // 토큰 관련
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_001", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOKEN_002", "저장된 리프레시 토큰이 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "TOKEN_003", "리프레시 토큰이 일치하지 않습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "TOKEN_004", "JWT가 유효하지 않습니다."),

    // 🎬 영화/콘텐츠 관련
    KMDB_NO_RESPONSE(HttpStatus.BAD_GATEWAY, "MOVIE_001", "KMDb 서버로부터 응답이 없습니다."),
    KMDB_SEARCH_FAILED(HttpStatus.BAD_GATEWAY, "MOVIE_002", "영화 검색 요청 중 오류가 발생했습니다."),
    MOVIE_DETAIL_FAILED(HttpStatus.BAD_GATEWAY, "MOVIE_003", "영화 상세 정보 요청 중 오류가 발생했습니다."),
    MOVIE_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "MOVIE_004", "해당 영화 상세 정보를 찾을 수 없습니다."),
    KMDB_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MOVIE_005", "KMDb 응답을 처리하는 중 오류가 발생했습니다."),

    // ⚙️ 시스템/서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001", "서버 오류입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "SYS_002", "잘못된 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "SYS_003", "허용되지 않은 HTTP 메서드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}