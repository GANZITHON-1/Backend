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
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ErrorStatus.SIGNUP_INVALID_PARAMETER);
        }
        // 2) 아이디 및 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail()) ||
                userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorStatus.SIGNUP_DUPLICATE);
        }
        // 3) 유저 저장
        String encodePassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .nickname(request.getNickname())
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
        User user = userRepository.findByNickname(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorStatus.LOGIN_INVALID_PARAMETER));
        //2) 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorStatus.LOGIN_INVALID_PARAMETER);
        }
        //3) JWT 토큰 생성
        String token = JwtUtil.generateToken(user.getNickname());

        //4) 로그인 성공 응답
        Map<String, Object> data = Map.of(
                "userId", user.getUserId(),
                "token", token
        );

        return Response.success(SuccessStatus.LOGIN_SUCCESS, data);

    }
}
