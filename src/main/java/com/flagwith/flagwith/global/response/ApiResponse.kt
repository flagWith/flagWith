package com.flagwith.flagwith.global.response

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun <T> ok(data: T?, message: String = "성공"): ApiResponse<T> = ApiResponse(true, message, data)
    }
}

// errors는 검증 실패일 때만 내려간다
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val success: Boolean = false,
    val errorCode: String,
    val message: String,
    val errors: List<FieldError>? = null,
) {
    data class FieldError(val field: String, val message: String)
}
