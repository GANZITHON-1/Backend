package com.likelion.ganzithon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(

        @Schema(description = "닉네임으로 로그인", example = "hong123")
        String userId,

        @Schema(description = "사용자 비밀번호", example = "test123")
        String password
) {}
