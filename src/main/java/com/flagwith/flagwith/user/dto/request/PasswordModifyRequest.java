package com.flagwith.flagwith.user.dto.request;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PasswordModifyRequest {
    private String currentPassword;
    private String newPassword;
}
