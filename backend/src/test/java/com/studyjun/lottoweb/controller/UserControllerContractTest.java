package com.studyjun.lottoweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyjun.lottoweb.dto.request.RefreshTokenRequest;
import com.studyjun.lottoweb.dto.request.SignInRequest;
import com.studyjun.lottoweb.dto.request.SignUpRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.AuthResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.CustomExceptionHandler;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.service.EmailService;
import com.studyjun.lottoweb.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerContractTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new CustomExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @DisplayName("성공 응답 계약: signUp은 check=true와 data.message를 반환한다")
    @Test
    void signUp_success_contract() throws Exception {
        ApiResponse body = ApiResponse.success(Message.builder().message("회원가입에 성공하였습니다.").build());
        doReturn((ResponseEntity<?>) ResponseEntity.status(HttpStatus.CREATED).body(body))
                .when(userService).signUp(any(SignUpRequest.class));

        SignUpRequest request = new SignUpRequest();
        request.setName("tester");
        request.setEmail("tester@test.com");
        request.setPassword("Passw0rd!");

        mockMvc.perform(post("/api/user/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.check").value(true))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.message").value("회원가입에 성공하였습니다."));
    }

    @DisplayName("성공 응답 계약: signIn은 check=true와 토큰 필드를 반환한다")
    @Test
    void signIn_success_contract() throws Exception {
        ApiResponse body = ApiResponse.success(
                AuthResponse.builder().accessToken("access-token").refreshToken("refresh-token").build()
        );
        doReturn((ResponseEntity<?>) ResponseEntity.ok(body)).when(userService).signIn(any(SignInRequest.class));

        SignInRequest request = new SignInRequest();
        request.setEmail("tester@test.com");
        request.setPassword("Passw0rd!");

        mockMvc.perform(post("/api/user/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.check").value(true))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @DisplayName("성공 응답 계약: refresh는 check=true와 토큰 필드를 반환한다")
    @Test
    void refresh_success_contract() throws Exception {
        ApiResponse body = ApiResponse.success(
                AuthResponse.builder().accessToken("new-access").refreshToken("new-refresh").build()
        );
        doReturn((ResponseEntity<?>) ResponseEntity.ok(body)).when(userService).refresh(any(RefreshTokenRequest.class));

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        mockMvc.perform(post("/api/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.check").value(true))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.accessToken").value("new-access"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @DisplayName("실패 응답 계약: signIn 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void signIn_businessException_errorContract() throws Exception {
        when(userService.signIn(any(SignInRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "로그인 실패"));

        SignInRequest request = new SignInRequest();
        request.setEmail("tester@test.com");
        request.setPassword("Passw0rd!");

        mockMvc.perform(post("/api/user/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value("로그인 실패"))
                .andExpect(jsonPath("$.path").value("/api/user/signIn"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @DisplayName("실패 응답 계약: signIn validation 실패 시 ErrorResponse.code를 반환한다")
    @Test
    void signIn_validationFailure_errorContract() throws Exception {
        SignInRequest invalid = new SignInRequest();
        invalid.setEmail("not-email");
        invalid.setPassword("");

        mockMvc.perform(post("/api/user/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").value("/api/user/signIn"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}