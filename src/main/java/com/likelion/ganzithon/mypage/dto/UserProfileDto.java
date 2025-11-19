package com.likelion.ganzithon.mypage.dto;

public record UserProfileDto(
        //수정 후 사용자 정보 전달
        Long userId,
        String name,
        String email
) {}
