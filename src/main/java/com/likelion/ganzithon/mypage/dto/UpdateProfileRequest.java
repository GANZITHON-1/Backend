package com.likelion.ganzithon.mypage.dto;

public record UpdateProfileRequest(
        //수정 데이터
        String name,
        String email
) {}
