package com.flagwith.flagwith.global.exception

import com.flagwith.flagwith.global.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BusinessException::class)
    fun business(e: BusinessException) = e.errorCode.toResponse()

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val code = ErrorCode.VALIDATION_FAILED
        val errors = e.bindingResult.fieldErrors.map {
            ErrorResponse.FieldError(it.field.toSnakeCase(), it.defaultMessage ?: "올바르지 않은 값입니다.")
        }
        return ResponseEntity.status(code.status)
            .body(ErrorResponse(errorCode = code.name, message = code.message, errors = errors))
    }

    // ponytail: 404/405/JSON 파싱 오류도 여기서 500이 된다. 필요해지면 해당 예외 핸들러를 추가.
    @ExceptionHandler(Exception::class)
    fun unexpected(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("처리되지 않은 예외", e) // 스택트레이스는 로그에만, 응답에는 내보내지 않음
        return ErrorCode.INTERNAL_ERROR.toResponse()
    }

    private fun ErrorCode.toResponse() =
        ResponseEntity.status(status).body(ErrorResponse(errorCode = name, message = message))

    private fun String.toSnakeCase() = replace(Regex("([a-z0-9])([A-Z])"), "$1_$2").lowercase()
}
