package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.dto.response.ErrorResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.security.CurrentUser;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Authorization Admin API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "전체 유저 리스트", description = "전체 유저의 리스트를 불러옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/user-list")
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "원하시는 page(int)를 입력해주세요.", required = true) @RequestParam int page
    ) {
        return adminService.getAllUsers(userPrincipal, page);
    }

    @Operation(summary = "특정 유저 활동 내역", description = "특정 유저의 상세 활동 내용을 확인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/user-detail")
    public ResponseEntity<?> getUserHistory(
            @Parameter(description = "User의 id(PK)를 입력해주세요.", required = true) @RequestParam Long id,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "원하시는 page(int)를 입력해주세요.", required = true) @RequestParam int page
    ) {
        return adminService.getUserHistory(id, userPrincipal, page);
    }

    @Operation(summary = "미응답 질문 리스트", description = "응답이 달리지 않은 질문 리스트를 불러옵니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/unanswered-questions")
    public ResponseEntity<?> getUnansweredQuestions(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "원하시는 page(int)를 입력해주세요.", required = true) @RequestParam int page
    ) {
        return adminService.getUnansweredQuestions(userPrincipal, page);
    }

    @Operation(summary = "유저 삭제", description = "특정 유저를 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "불러오기 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "불러오기 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping(value = "/user-delete")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User의 id(PK)를 입력해주세요.", required = true) @RequestParam long id,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return adminService.deleteUser(id, userPrincipal);
    }
}