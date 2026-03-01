
package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.CustomExceptionHandler;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.exception.ServerErrorCode;
import com.studyjun.lottoweb.service.WekaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WekaControllerContractTest {

    @Mock
    private WekaService wekaService;

    @InjectMocks
    private WekaController wekaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(wekaController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @DisplayName("성공 계약: top6는 success=true와 data 배열을 반환한다")
    @Test
    void top6_success_contract() throws Exception {
        doReturn((ResponseEntity<?>) ResponseEntity.ok(ApiResponse.success(List.of(1, 2, 3, 4, 5, 6)))).when(wekaService).top6Frequencies(any());

        mockMvc.perform(get("/api/lotto/top6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("성공 계약: random은 success=true와 data 배열을 반환한다")
    @Test
    void random_success_contract() throws Exception {
        doReturn((ResponseEntity<?>) ResponseEntity.ok(ApiResponse.success(List.of(7, 8, 9, 10, 11, 12)))).when(wekaService).generateRandom(any());

        mockMvc.perform(get("/api/lotto/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("성공 계약: user-lotto-info는 success=true와 data 객체를 반환한다")
    @Test
    void userLottoInfo_success_contract() throws Exception {
        doReturn((ResponseEntity<?>) ResponseEntity.ok(ApiResponse.success(Map.of("userEmail", "tester@test.com"))))
                .when(wekaService).getCurrentUserLottoInfo(any());

        mockMvc.perform(get("/api/lotto/user-lotto-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.userEmail").value("tester@test.com"));
    }

    @DisplayName("실패 계약: pattern-recognition 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void patternRecognition_failure_contract() throws Exception {
        when(wekaService.patternRecognition(anyString(), any()))
                .thenThrow(new BusinessException(ServerErrorCode.INTERNAL_SERVER_ERROR, "패턴 분석 중 오류"));

        mockMvc.perform(get("/api/lotto/pattern-recognition").param("date", "2025-01-04"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ServerErrorCode.INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.path").value("/api/lotto/pattern-recognition"));
    }

    @DisplayName("실패 계약: ensemble 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void ensemble_failure_contract() throws Exception {
        when(wekaService.ensembleLottoPrediction(anyString(), any()))
                .thenThrow(new BusinessException(ServerErrorCode.INTERNAL_SERVER_ERROR, "앙상블 예측 오류"));

        mockMvc.perform(get("/api/lotto/ensemble").param("date", "2025-01-04"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ServerErrorCode.INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.path").value("/api/lotto/ensemble"));
    }
}