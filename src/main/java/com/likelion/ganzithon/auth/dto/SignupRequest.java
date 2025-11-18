package com.likelion.ganzithon.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String email;
    private String nickname;
    private String password;
    private String passwordConfirm; //비밀번호 체크
}
