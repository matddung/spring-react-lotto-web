package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.dto.response.ErrorResponse;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.security.CurrentUser;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.service.WekaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Weka", description = "Authorization Recommended Winning Number API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lotto")
public class WekaController {
    private final WekaService wekaService;

    @Operation(summary = "빈도 수 상위", description = "전체 번호의 빈도 수를 측정하고, 상위 6개의 번호를 반환합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/top6")
    public ResponseEntity<?> getStatisticsTop6(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.top6Frequencies(userPrincipal);
    }

    @Operation(summary = "패턴 분석 알고리즘", description = "패턴을 분석 후, 예측된 결과 값을 반환합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/pattern-recognition")
    public ResponseEntity<?> getPatternRecognition(
            @Parameter(description = "토요일의 날짜를 입력해주세요.", required = true) @RequestParam String date,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.patternRecognition(date, userPrincipal);
    }

    @Operation(summary = "랜덤 알고리즘", description = "랜덤한 값을 반환합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/random")
    public ResponseEntity<?> generateRandomNumbers(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.generateRandom(userPrincipal);
    }

    @Operation(summary = "앙상블 알고리즘", description = "앙상블 알고리즘을통해 도출된 번호 6개를 반환합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/ensemble")
    public ResponseEntity<?> getEnsemblePrediction(
            @Parameter(description = "토요일의 날짜를 입력해주세요.", required = true) @RequestParam String date,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.ensembleLottoPrediction(date, userPrincipal);
    }

    @Operation(summary = "몬테카를로 알고리즘", description = "몬테카를로 알고리즘을 통해 도출된 번호 6개를 반환합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/monte-carlo")
    public ResponseEntity<?> runMonteCarloSimulation(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.monteCarloSimulation(userPrincipal);
    }

    @Operation(summary = "lotto 발급 내역 확인", description = "유저의 금주 lotto 발급 내역을 확인합니다")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/user-lotto-info")
    public ResponseEntity<?> getCurrentUserLottoInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.getCurrentUserLottoInfo(userPrincipal);
    }
}