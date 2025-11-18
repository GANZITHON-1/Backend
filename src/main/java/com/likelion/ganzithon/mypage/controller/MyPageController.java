package com.likelion.ganzithon.mypage.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.mypage.dto.UpdateProfileRequest;
import com.likelion.ganzithon.mypage.dto.UserProfileDto;
import com.likelion.ganzithon.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @PutMapping("/profile")
    public Response<UserProfileDto> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return myPageService.updateProfile(userId, request);
    }
}
