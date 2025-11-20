package com.likelion.ganzithon.auth.controller;

import com.likelion.ganzithon.auth.dto.DeleteUserRequest;
import com.likelion.ganzithon.auth.dto.LoginRequest;
import com.likelion.ganzithon.auth.dto.LogoutRequest;
import com.likelion.ganzithon.auth.dto.SignupRequest;
import com.likelion.ganzithon.auth.service.AuthService;
import com.likelion.ganzithon.exception.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        Response<?> response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "닉네임과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "JWT 토큰을 검증하고 로그아웃 처리합니다.")
    public Response<?> logout(@RequestBody LogoutRequest request) {
        return authService.logout(request.token());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "JWT 토큰 검증 후 사용자 계정을 삭제합니다.")
    public Response<?> deleteUser(@RequestBody DeleteUserRequest request) {
        return authService.deleteUser(request.userId(), request.token());
    }

}
