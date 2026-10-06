package com.flagwith.flagwith.user.controller;

import com.flagwith.flagwith.global.response.ApiResponse;
import com.flagwith.flagwith.user.dto.response.UserProfileResponse;
import com.flagwith.flagwith.user.service.UserService;
import com.flagwith.flagwith.user.dto.request.PasswordModifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/profile")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
// 인증 기능 구현 후
//    @PatchMapping("/modify/password")
//    public ApiResponse<UserProfileResponse> modifyPassword(@RequestBody PasswordModifyRequest passwordModifyRequest) {
//    }
//
//    @PatchMapping("/modify/username")
//    public ApiResponse<UserProfileResponse> modifyUsername(@RequestBody Map<String, String> usernameModifyRequest) {
//
//    }

}
