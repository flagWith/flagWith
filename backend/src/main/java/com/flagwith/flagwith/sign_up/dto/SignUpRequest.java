package com.flagwith.flagwith.sign_up.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String username;
    private String password;
    private String passwordConfirm;

}
