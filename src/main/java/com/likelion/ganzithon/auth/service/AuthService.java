package com.likelion.ganzithon.auth.service;

import com.likelion.ganzithon.auth.dto.LoginRequest;
import com.likelion.ganzithon.auth.dto.SignupRequest;
import com.likelion.ganzithon.auth.entity.User;
import com.likelion.ganzithon.auth.jwt.JwtUtil;
import com.likelion.ganzithon.auth.repository.UserRepository;
import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public Response<?> signup(SignupRequest request) {
        // 1) 비밀번호 일치 검사
        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(ErrorStatus.SIGNUP_INVALID_PARAMETER);
        }
        // 2) 아이디 및 이메일 중복 체크
        if (userRepository.existsByEmail(request.email()) ||
                userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorStatus.SIGNUP_DUPLICATE);
        }
        // 3) 유저 저장
        String encodePassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .nickname(request.nickname())
                .password(encodePassword)
                .build();

        User saved = userRepository.save(user);

        // 4) 회원가입 성공 응답
        Map<String, Object> data = Map.of(
                "userId", saved.getUserId(),
                "name", saved.getName(),
                "email", saved.getEmail(),
                "nickname", saved.getNickname()
        );

        return Response.success(SuccessStatus.SIGNUP_SUCCESS, data);

    }

    //로그인
    public Response<?> login(LoginRequest request) {
        //1) 닉네임으로(userId)으로 사용자 조회
        User user = userRepository.findByNickname(request.userId())
                .orElseThrow(() -> new CustomException(ErrorStatus.LOGIN_INVALID_PARAMETER));
        //2) 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorStatus.LOGIN_INVALID_PARAMETER);
        }
        //3) JWT 토큰 생성
        // userId를 저장하여 userId로 직접 조회 가능하도록 수정
        String token = JwtUtil.generateToken(user.getUserId().toString());

        //4) 로그인 성공 응답
        Map<String, Object> data = Map.of(
                "userId", user.getUserId(),
                "token", token,
                "name", user.getName(),
                "email", user.getEmail()
        );

        return Response.success(SuccessStatus.LOGIN_SUCCESS, data);
    }

    // 로그아웃
    public Response<?> logout(String token) {

        // 1) 토큰 null 또는 빈 값 체크
        if (token == null || token.isBlank()) {
            throw new CustomException(ErrorStatus.LOGOUT_INVALID_TOKEN);
        }

        // 2) 토큰 유효성 검사
        if (!JwtUtil.validateToken(token)) {
            throw new CustomException(ErrorStatus.LOGOUT_INVALID_TOKEN);
        }

        // 3) 유효하면 그냥 성공 응답 (서버 상태 없음)
        return Response.success(SuccessStatus.LOGOUT_SUCCESS, null);
    }

    // 회원 탈퇴
    public Response<?> deleteUser(Long userId, String token) {
        // 1) 토큰 null 또는 유효성 검사
        if (token == null || token.isBlank() || !JwtUtil.validateToken(token)) {
            throw new CustomException(ErrorStatus.DELETE_INVALID_TOKEN);
        }

        // 2) 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 3) 사용자 삭제
        userRepository.deleteById(userId);

        // 4) 성공 응답
        return Response.success(SuccessStatus.DELETE_SUCCESS, null);
    }
}
