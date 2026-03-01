package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.exception.*;
import com.studyjun.lottoweb.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerContractTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(adminController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @DisplayName("성공 계약: user-list는 success=true와 data.content를 반환한다")
    @Test
    void getAllUsers_success_contract() throws Exception {
        ApiResponse response = ApiResponse.success(Map.of("content", List.of(Map.of("id", 1L))));
        doReturn((ResponseEntity<?>) ResponseEntity.ok(response)).when(adminService).getAllUsers(any(), anyInt());

        mockMvc.perform(get("/api/admin/user-list").param("page", "0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @DisplayName("성공 계약: user-detail은 success=true와 user/question 필드를 반환한다")
    @Test
    void getUserHistory_success_contract() throws Exception {
        ApiResponse response = ApiResponse.success(Map.of("user", Map.of("id", 2L), "question", Map.of("content", List.of())));
        doReturn((ResponseEntity<?>) ResponseEntity.ok(response)).when(adminService).getUserHistory(anyLong(), any(), anyInt());

        mockMvc.perform(get("/api/admin/user-detail").param("id", "2").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.id").value(2))
                .andExpect(jsonPath("$.data.question.content").isArray());
    }

    @DisplayName("성공 계약: user-delete는 success=true와 data.message를 반환한다")
    @Test
    void deleteUser_success_contract() throws Exception {
        ApiResponse response = ApiResponse.success(Message.builder().message("유저 삭제가 완료되었습니다.").build());
        doReturn((ResponseEntity<?>) ResponseEntity.ok(response)).when(adminService).deleteUser(anyLong(), any());

        mockMvc.perform(delete("/api/admin/user-delete").param("id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("유저 삭제가 완료되었습니다."));
    }

    @DisplayName("실패 계약: user-list 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void getAllUsers_failure_contract() throws Exception {
        when(adminService.getAllUsers(any(), anyInt()))
                .thenThrow(new BusinessException(AuthErrorCode.FORBIDDEN, "ADMIN 계정이 아닙니다."));

        mockMvc.perform(get("/api/admin/user-list").param("page", "0"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.path").value("/api/admin/user-list"));
    }

    @DisplayName("실패 계약: user-delete 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void deleteUser_failure_contract() throws Exception {
        when(adminService.deleteUser(anyLong(), any()))
                .thenThrow(new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "사용자를 찾을 수 없습니다."));

        mockMvc.perform(delete("/api/admin/user-delete").param("id", "999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.path").value("/api/admin/user-delete"));
    }
}