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

    // 도메인별로

    // REPORT
    REPORT_CREATED(HttpStatus.CREATED, "REPORT201", "사고 제보가 생성되었습니다."),
    REPORT_UPDATED(HttpStatus.OK, "REPORT200", "사고 제보가 수정되었습니다."),
    REPORT_DELETED(HttpStatus.NO_CONTENT, "REPORT204", "사고 제보가 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
