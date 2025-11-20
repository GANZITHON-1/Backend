package com.likelion.ganzithon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SignupRequest(

        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @Schema(description = "사용자 이메일", example = "hong@example.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "hong123")
        String nickname,

        @Schema(description = "비밀번호", example = "test123")
        String password,

        @Schema(description = "비밀번호 확인", example = "test123")
        String passwordConfirm
        ) {
}