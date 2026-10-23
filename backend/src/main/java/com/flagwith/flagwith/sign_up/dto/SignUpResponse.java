package com.flagwith.flagwith.sign_up.dto;

import com.flagwith.flagwith.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpResponse {
    private final String email;
    private final String username;

    public static SignUpResponse from(User user) {
        return new SignUpResponse(user.getEmail(), user.getUsername());
    }
}
