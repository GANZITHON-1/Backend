package com.likelion.ganzithon.mypage.service;

import com.likelion.ganzithon.auth.entity.User;
import com.likelion.ganzithon.auth.repository.UserRepository;
import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.mypage.dto.MyReportDto;
import com.likelion.ganzithon.mypage.dto.UpdateProfileRequest;
import com.likelion.ganzithon.mypage.dto.UserProfileDto;
import com.likelion.ganzithon.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // 내 정보 수정
    @Transactional
    public Response<UserProfileDto> updateProfile(Long userId, UpdateProfileRequest request) {
        // 1) 기존 사용자 정보 조회
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 2) 기존 정보 기반으로 새 객체 생성 - setter 없이 업데이트
        User updatedUser = User.builder()
                .userId(oldUser.getUserId())  // 기존 ID 유지
                .name(request.name())          // 새 이름
                .email(request.email())        // 새 이메일
                .nickname(oldUser.getNickname()) // 기존 닉네임 유지
                .password(oldUser.getPassword()) // 기존 비밀번호 유지
                .build();

        // 3) DB에 업데이트 된 사용자 정보 저장
        userRepository.save(updatedUser);

        // 4) 응답
        UserProfileDto data = new UserProfileDto(
                updatedUser.getUserId(),
                updatedUser.getName(),
                updatedUser.getEmail()
        );
        return Response.success(SuccessStatus.PROFILE_UPDATE_SUCCESS, data);
    }

    // 제보 목록 조회
    @Transactional(readOnly = true)
    public Response<List<MyReportDto>> getMyReports(Long userId) {
        List<com.likelion.ganzithon.report.domain.Report> reports = reportRepository.findByUserId(userId);
        
        if (reports.isEmpty()) {
            throw new CustomException(ErrorStatus.MYREPORTS_NOT_FOUND);
        }
        List<MyReportDto> data = reports.stream()
                .map(MyReportDto::from)
                .toList();
        return Response.success(SuccessStatus.MYREPORTS_SUCCESS, data);
    }
}
