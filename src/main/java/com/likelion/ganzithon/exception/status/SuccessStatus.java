package com.likelion.ganzithon.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    // SUCCESS 2XX
    SUCCESS(HttpStatus.OK, "COMMON200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 성공적으로 생성되었습니다."),

    //회원가입
    SIGNUP_SUCCESS(HttpStatus.OK, "AUTH200", "회원가입 성공"),
    //로그인
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH200", "로그인 성공."),

    //PROFILE 2XX
    //회원정보 수정
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "PROFILE200", "성공적으로 수정되었습니다.");

    // 도메인별로
    private final HttpStatus status;
    private final String code;
    private final String message;
}
