package com.flagwith.flagwith.user.dto.response;


import com.flagwith.flagwith.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfileResponse {

    private final UserInfo user;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                new UserInfo(user.getId(), user.getEmail(), user.getUsername())
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class UserInfo {
        private final Long id;
        private final String email;
        private final String username;
    }
}
