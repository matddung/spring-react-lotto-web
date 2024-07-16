package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.security.CurrentUser;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.service.WekaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Weka", description = "Authorization Recommended Winning Number API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/lotto")
public class WekaController {
    private final WekaService wekaService;

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return wekaService.statistics();
    }

    @GetMapping("/top6")
    public ResponseEntity<?> getStatisticsTop6(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.top6Frequencies(userPrincipal);
    }

    @GetMapping("/pattern-recognition")
    public ResponseEntity<?> getPatternRecognition(
            @Parameter(description = "토요일의 날짜를 입력해주세요.", required = true) @RequestParam String date,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            return wekaService.patternRecognition(date, userPrincipal);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing pattern recognition logic");
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> generateRandomNumbers(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.generateRandom(userPrincipal);
    }

    @GetMapping("/ensemble")
    public ResponseEntity<?> getEnsemblePrediction(
            @Parameter(description = "토요일의 날짜를 입력해주세요.", required = true) @RequestParam String date,
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        try {
            return wekaService.ensembleLottoPrediction(date, userPrincipal);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing ensemble prediction logic");
        }
    }

    @GetMapping("/monte-carlo")
    public ResponseEntity<?> runMonteCarloSimulation(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.monteCarloSimulation(userPrincipal);
    }

    @GetMapping("/user-lotto-info")
    public ResponseEntity<?> getCurrentUserLottoInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return wekaService.getCurrentUserLottoInfo(userPrincipal);
    }
}