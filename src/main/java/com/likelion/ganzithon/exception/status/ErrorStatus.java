package com.likelion.ganzithon.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // COMMON 4XX
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400", "파라미터가 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON400", "요청 본문이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 리소스입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "사용자를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP Method입니다."),

    //AUTH 4XX
    //회원가입
    SIGNUP_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "AUTH400", "비밀번호가 일치하지 않습니다."), // 비밀번호 일치 실패
    SIGNUP_DUPLICATE(HttpStatus.CONFLICT, "AUTH409", "이미 존재하는 이메일 또는 닉네임입니다."),    // 이메일 및 아이디 중복 체크 실패
    //로그인
    LOGIN_INVALID_PARAMETER(HttpStatus.UNAUTHORIZED, "AUTH401", "아이디 또는 비밀번호가 일치하지 않습니다."),

    //PROFILE 4XX
    //회원정보 수정
    PROFILE_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "PROFILE400", "이메일 형식이 올바르지 않습니다.");

    // 도메인별로

    private final HttpStatus status;
    private final String code;
    private final String message;
}
