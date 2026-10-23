package com.flagwith.flagwith.login.dto;


import com.flagwith.flagwith.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponse {
    @Getter
    @RequiredArgsConstructor
    public static class UserInfo {
        private final Long id;
        private final String email;
        private final String username;
    }

    private final UserInfo user;


    public static LoginResponse from(User user) {
        return new LoginResponse(
                new UserInfo(user.getId(), user.getEmail(), user.getUsername()));
    }


}

