package com.likelion.ganzithon.auth.dto;

public record SignupRequest(
        String name,
        String email,
        String nickname,
        String password,
        String passwordConfirm //비밀번호 체크
) {}
