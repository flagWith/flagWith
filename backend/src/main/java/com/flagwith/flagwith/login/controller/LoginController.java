package com.flagwith.flagwith.login.controller;

import com.flagwith.flagwith.global.response.ApiResponse;
import com.flagwith.flagwith.login.dto.LoginRequest;
import com.flagwith.flagwith.login.dto.LoginResponse;
import com.flagwith.flagwith.login.service.LoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/login")
@RequiredArgsConstructor
public class LoginController {
    private static final int AGE = 60 * 60 * 24 * 3;

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        LoginResponse loginResponse = loginService.Login(loginRequest);

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId", loginResponse.getUser().getId());
        session.setMaxInactiveInterval(AGE);
        return ResponseEntity.ok(ApiResponse.ok(loginResponse));
    }

}
