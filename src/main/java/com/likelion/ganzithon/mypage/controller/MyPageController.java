package com.likelion.ganzithon.mypage.controller;

import com.likelion.ganzithon.auth.entity.User;
import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.mypage.dto.MyReportDto;
import com.likelion.ganzithon.mypage.dto.UpdateProfileRequest;
import com.likelion.ganzithon.mypage.dto.UserProfileDto;
import com.likelion.ganzithon.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MyPage API", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보(이름, 이메일)를 수정합니다.")
    @PutMapping("/profile")
    public Response<UserProfileDto> updateProfile(@RequestBody UpdateProfileRequest request, @AuthenticationPrincipal User user) {
        return myPageService.updateProfile(user.getUserId(), request);
    }

    @Operation(summary = "제보 목록 조회", description = "사용자가 작성한 제보 목록을 조회합니다.")
    @GetMapping("/reports")
    public Response<List<MyReportDto>> getMyReports(@RequestParam Long userId) {
        return myPageService.getMyReports(userId);
    }
}