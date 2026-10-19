package com.flagwith.flagwith.global

import com.flagwith.flagwith.global.exception.BusinessException
import com.flagwith.flagwith.global.exception.ErrorCode
import com.flagwith.flagwith.global.exception.GlobalExceptionHandler
import com.flagwith.flagwith.global.response.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class TeamRequest(@field:NotBlank val teamName: String?)

data class TeamResponse(val teamName: String)

@RestController
class ProbeController {
    @GetMapping("/probe/ok")
    fun ok() = ApiResponse.ok(TeamResponse("a"))

    @GetMapping("/probe/missing")
    fun missing(): Nothing = throw BusinessException(ErrorCode.TEAM_NOT_FOUND)

    @PostMapping("/probe/validate")
    fun validate(@Valid @RequestBody req: TeamRequest) = ApiResponse.ok(null)
}

@WebMvcTest(ProbeController::class)
@Import(GlobalExceptionHandler::class)
class GlobalExceptionHandlerTest(@Autowired val mvc: MockMvc) {

    @Test
    fun `성공 응답은 snake_case이고 data가 항상 있다`() {
        mvc.perform(get("/probe/ok"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.team_name").value("a"))
    }

    @Test
    fun `비즈니스 예외는 실제 HTTP 상태와 error_code를 내리고 errors는 없다`() {
        mvc.perform(get("/probe/missing"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error_code").value("TEAM_NOT_FOUND"))
            .andExpect(jsonPath("$.errors").doesNotExist())
    }

    @Test
    fun `검증 실패는 VALIDATION_FAILED와 snake_case 필드명 errors를 내린다`() {
        mvc.perform(post("/probe/validate").contentType(MediaType.APPLICATION_JSON).content("""{"team_name":""}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error_code").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.errors[0].field").value("team_name"))
    }
}
