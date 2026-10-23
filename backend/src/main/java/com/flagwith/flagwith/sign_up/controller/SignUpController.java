package com.flagwith.flagwith.sign_up.controller;

import com.flagwith.flagwith.global.response.ApiResponse;
import com.flagwith.flagwith.sign_up.dto.SignUpRequest;
import com.flagwith.flagwith.sign_up.dto.SignUpResponse;
import com.flagwith.flagwith.sign_up.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signupService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignUpResponse>> signup(@RequestBody SignUpRequest signUpRequest) {
        SignUpResponse response = signupService.signUp(signUpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "회원가입 성공"));
    }
}
