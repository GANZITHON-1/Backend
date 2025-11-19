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

    //POFILE
    PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "PROFILE200", "성공적으로 수정되었습니다."),
    
    //MYPAGE
    MYREPORTS_SUCCESS(HttpStatus.OK, "MYREPORTS200", "제보 목록 조회 성공"),

    // REPORT
    REPORT_CREATED(HttpStatus.CREATED, "REPORT201", "사고 제보가 생성되었습니다."),
    REPORT_UPDATED(HttpStatus.OK, "REPORT200", "사고 제보가 수정되었습니다."),
    REPORT_DELETED(HttpStatus.NO_CONTENT, "REPORT204", "사고 제보가 삭제되었습니다."),

    // AI
    AI_REPORT_DETAIL(HttpStatus.OK, "AI200", "RAG 기반 사고 제보 상세 및 요약 조회 성공"),
    AI_FALLBACK_SUMMARY(HttpStatus.OK, "AI201", "RAG 검색 결과가 없어 기본 요약 모델로 처리되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
