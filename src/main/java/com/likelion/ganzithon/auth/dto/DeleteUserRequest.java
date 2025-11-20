package com.likelion.ganzithon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeleteUserRequest(

        @Schema(description = "탈퇴할 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 JWT 토큰", example = "jwtToken")
        String token
) {}
