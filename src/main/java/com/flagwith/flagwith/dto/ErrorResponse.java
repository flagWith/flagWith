package com.flagwith.flagwith.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final boolean success;
    private final String errorCode;
    private final String message;
}
