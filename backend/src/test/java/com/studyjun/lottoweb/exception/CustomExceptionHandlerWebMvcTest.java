package com.studyjun.lottoweb.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomExceptionHandlerWebMvcTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new CustomExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @DisplayName("예상하지 못한 예외 발생 시 공통 에러 응답 포맷을 반환한다")
    @Test
    void shouldReturnErrorResponseFormatForUnhandledException() throws Exception {
        mockMvc.perform(get("/test/errors/unhandled"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ServerErrorCode.INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ServerErrorCode.INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.path").value("/test/errors/unhandled"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @DisplayName("BusinessException 발생 시 공통 에러 응답 포맷을 반환한다")
    @Test
    void shouldReturnErrorResponseFormatForBusinessException() throws Exception {
        mockMvc.perform(get("/test/errors/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.path").value("/test/errors/business"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @DisplayName("Validation 실패 시 INVALID_INPUT_VALUE 에러를 반환한다")
    @Test
    void shouldReturnInvalidInputWhenValidationFails() throws Exception {
        mockMvc.perform(post("/test/errors/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ValidationRequest(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value("name: 이름은 필수입니다."));
    }

    @DisplayName("BusinessException 메시지를 그대로 반환한다")
    @Test
    void shouldReturnBusinessExceptionMessage() throws Exception {
        mockMvc.perform(get("/test/errors/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비즈니스 예외 테스트"));
    }

    @RestController
    @RequestMapping("/test/errors")
    static class TestController {

        @GetMapping("/unhandled")
        String unhandled() {
            throw new IllegalStateException("예상하지 못한 오류");
        }

        @GetMapping("/business")
        String business() {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "비즈니스 예외 테스트");
        }

        @PostMapping("/validation")
        String validation(@Valid @RequestBody ValidationRequest request) {
            return "ok";
        }
    }

    static class ValidationRequest {
        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        public ValidationRequest() {
        }

        public ValidationRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}