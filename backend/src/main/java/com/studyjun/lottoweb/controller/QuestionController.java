package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.dto.request.CreateAnswerRequest;
import com.studyjun.lottoweb.dto.request.CreateQuestionRequest;
import com.studyjun.lottoweb.dto.response.ApiResponseFactory;
import com.studyjun.lottoweb.dto.response.ErrorResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.security.CurrentUser;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Question", description = "Authorization User Question API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;
    private final ApiResponseFactory apiResponseFactory;

    @Operation(summary = "고객센터 질문 등록", description = "고객센터에 질문 글을 남깁니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "등록 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping(value = "/create")
    public ResponseEntity<?> createQuestion(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 CreateQuestionRequest를 참고해주세요.", required = true) @Valid @RequestBody CreateQuestionRequest createQuestionRequest
    ) {
        Message response = questionService.createQuestion(userPrincipal, createQuestionRequest);
        return apiResponseFactory.ok(response);
    }

    @Operation(summary = "고객센터 질문 목록", description = "고객센터에 있는 전체 질문 리스트를 불러옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/list")
    public ResponseEntity<?> getAllQuestions(
            @Parameter(description = "원하시는 page(int)를 입력해주세요.", required = true) @RequestParam int page
    ) {
        return apiResponseFactory.ok(questionService.getAllQuestions(page));
    }

    @Operation(summary = "내 고객센터 질문 목록", description = "고객센터에 있는 내 질문 리스트를 불러옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/my-list")
    public ResponseEntity<?> getMyQuestions(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "원하시는 page(int)를 입력해주세요.", required = true) @RequestParam int page
    ) {
        return apiResponseFactory.ok(questionService.getMyQuestions(userPrincipal, page));
    }

    @Operation(summary = "고객센터 질문 보기", description = "질문을 상세히 봅니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/detail")
    public ResponseEntity<?> showQuestionDetail(
            @Parameter(description = "Question의 id(PK)를 입력해주세요", required = true) @RequestParam long id,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return apiResponseFactory.ok(questionService.showQuestionDetail(id, userPrincipal));
    }

    @Operation(summary = "질문에 답변 달기(ADMIN 전용)", description = "질문에 답변을 답니다. ADMIN 계정만 사용할 수 있는 기능입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "답변달기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "답변달기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping(value = "/answer")
    public ResponseEntity<?> createAnswer(
            @Parameter(description = "Question의 Primary Key(id)를 입력해주세요.", required = true) @RequestParam long id,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 CreateAnswerRequest를 참고해주세요.", required = true) @Valid @RequestBody CreateAnswerRequest createAnswerRequest
    ) {
        Message response = questionService.createAnswer(id, userPrincipal, createAnswerRequest);
        return apiResponseFactory.ok(response);
    }
}