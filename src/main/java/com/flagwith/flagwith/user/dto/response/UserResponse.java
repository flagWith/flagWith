package com.flagwith.flagwith.user.dto.response;

import com.flagwith.flagwith.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {
    private final Long id;
    private final String email;
    private final String username;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getUsername());
    }

}
