package com.likelion.ganzithon.auth.dto;

public record LoginRequest (
        String userId,   //nickname으로 로그인
        String password
){}
