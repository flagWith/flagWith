package com.flagwith.flagwith.sign_up.service;

import com.flagwith.flagwith.global.exception.BusinessException;
import com.flagwith.flagwith.global.exception.ErrorCode;
import com.flagwith.flagwith.sign_up.dto.SignUpRequest;
import com.flagwith.flagwith.sign_up.dto.SignUpResponse;
import com.flagwith.flagwith.sign_up.repository.SignUpRepository;
import com.flagwith.flagwith.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9_+&*-]+(?:\\.[A-Za-z0-9_+&*-]+)*@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$");

    //8자 이상 128자 이하, 대/소문자  ·숫자·특수문자 포함
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,128}$");

    private final SignUpRepository signUpRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (signUpRequest == null || isBlank(signUpRequest.getEmail()) || isBlank(signUpRequest.getUsername())
                || isBlank(signUpRequest.getPassword()) || isBlank(signUpRequest.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.MISSING_FILED);
        }

        if (signUpRequest.getUsername().length() < 2 || signUpRequest.getUsername().length() > 20) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED);
        }

        if (!EMAIL_PATTERN.matcher(signUpRequest.getEmail()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }

        if (!PASSWORD_PATTERN.matcher(signUpRequest.getPassword()).matches()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED);
        }

        if (!signUpRequest.getPassword().equals(signUpRequest.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (signUpRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BusinessException(ErrorCode.CONFLICTED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .password(encodedPassword)
                .build();

        User savedUser = signUpRepository.save(user);
        return SignUpResponse.from(savedUser);
    }

}
