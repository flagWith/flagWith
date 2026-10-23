package com.flagwith.flagwith.login.service;

import com.flagwith.flagwith.global.exception.BusinessException;
import com.flagwith.flagwith.global.exception.ErrorCode;
import com.flagwith.flagwith.login.dto.LoginRequest;
import com.flagwith.flagwith.login.dto.LoginResponse;
import com.flagwith.flagwith.login.repository.LoginRepository;
import com.flagwith.flagwith.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private final LoginAttemptService loginAttemptService;


    @Transactional
    public LoginResponse Login(LoginRequest loginRequest) {
        if (loginRequest == null || isBlank(loginRequest.getEmail()) || isBlank(loginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.MISSING_FILED);
        }

        String email = loginRequest.getEmail().trim();

        if (!EMAIL_PATTERN.matcher(loginRequest.getEmail()).matches()) {
            throw new BusinessException(ErrorCode.INVALID_FORMAT);
        }

        if (loginAttemptService.isLocked(email)) {
            throw new BusinessException(ErrorCode.LOGIN_RATE_LIMITED);
        }

        var userOptional = loginRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            loginAttemptService.recordFailure(email);
            throw new BusinessException(ErrorCode.AUTH_FAILED);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(email);
            throw new BusinessException(ErrorCode.INVALID_FORMAT);
        }

        loginAttemptService.clearFailures(email);

        return LoginResponse.from(user);

    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
