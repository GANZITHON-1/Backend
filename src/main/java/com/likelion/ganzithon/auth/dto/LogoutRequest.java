package com.likelion.ganzithon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogoutRequest(

        @Schema(description = "로그아웃할 사용자의 JWT 토큰", example = "jwt-token")
        String token

) {}
