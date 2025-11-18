package com.likelion.ganzithon.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String userId;    //nickname으로 로그인
    private String password;
}
