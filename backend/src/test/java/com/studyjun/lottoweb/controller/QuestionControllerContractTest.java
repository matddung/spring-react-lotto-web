package com.studyjun.lottoweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyjun.lottoweb.dto.request.CreateAnswerRequest;
import com.studyjun.lottoweb.dto.request.CreateQuestionRequest;
import com.studyjun.lottoweb.dto.response.*;
import com.studyjun.lottoweb.exception.*;
import com.studyjun.lottoweb.service.QuestionService;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuestionControllerContractTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private ApiResponseFactory apiResponseFactory;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(questionController)
                .setControllerAdvice(new CustomExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @DisplayName("성공 계약: create 질문 생성은 success=true와 data.message를 반환한다")
    @Test
    void createQuestion_success_contract() throws Exception {
        Message message = Message.builder().message("고객센터에 질문이 등록되었습니다.").build();
        ApiResponse response = ApiResponse.success(message);
        when(questionService.createQuestion(any(), any(CreateQuestionRequest.class))).thenReturn(message);
        when(apiResponseFactory.ok(any())).thenReturn(ResponseEntity.ok(response));

        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setSubject("문의");
        request.setContent("문의 내용");
        request.setIsPrivate(false);

        mockMvc.perform(post("/api/question/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("고객센터에 질문이 등록되었습니다."));
    }

    @DisplayName("성공 계약: list는 success=true와 data.content를 반환한다")
    @Test
    void getAllQuestions_success_contract() throws Exception {
        QuestionPageResponse pageResponse = QuestionPageResponse.builder()
                .content(List.of(QuestionSummaryResponse.builder().id(1L).subject("제목").authorNickname("user").isPrivate(false).createdDate(LocalDateTime.now()).build()))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .build();
        ApiResponse response = ApiResponse.success(pageResponse);
        when(questionService.getAllQuestions(anyInt())).thenReturn(pageResponse);
        when(apiResponseFactory.ok(any())).thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(get("/api/question/list").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @DisplayName("성공 계약: answer 생성은 success=true와 data.message를 반환한다")
    @Test
    void createAnswer_success_contract() throws Exception {
        Message message = Message.builder().message("답변이 등록되었습니다.").build();
        ApiResponse response = ApiResponse.success(message);
        when(questionService.createAnswer(anyLong(), any(), any(CreateAnswerRequest.class))).thenReturn(message);
        when(apiResponseFactory.ok(any())).thenReturn(ResponseEntity.ok(response));

        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setSubject("답변 제목");
        request.setContent("답변 내용");

        mockMvc.perform(post("/api/question/answer")
                        .param("id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("답변이 등록되었습니다."));
    }

    @DisplayName("실패 계약: detail 비즈니스 예외 시 ErrorResponse.code를 반환한다")
    @Test
    void showQuestionDetail_failure_contract() throws Exception {
        when(questionService.showQuestionDetail(anyLong(), any()))
                .thenThrow(new BusinessException(AuthErrorCode.FORBIDDEN, "비밀글입니다."));

        mockMvc.perform(get("/api/question/detail").param("id", "3"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(AuthErrorCode.FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.path").value("/api/question/detail"));
    }

    @DisplayName("실패 계약: create validation 실패 시 ErrorResponse.code를 반환한다")
    @Test
    void createQuestion_validationFailure_contract() throws Exception {
        CreateQuestionRequest invalid = new CreateQuestionRequest();
        invalid.setSubject("");
        invalid.setContent("");

        mockMvc.perform(post("/api/question/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(CommonErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.path").value("/api/question/create"));
    }
}