package com.studyjun.lottoweb.controller;

import com.studyjun.lottoweb.service.WekaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Weka", description = "Authorization Recommended Winning Number API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class WekaController {
    private final WekaService wekaService;

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return wekaService.statistics();
    }

    @GetMapping("/top6")
    public ResponseEntity<?> getStatisticsTop6() {
        return wekaService.top6Frequencies();
    }

    @GetMapping("/pattern-recognition")
    public ResponseEntity<?> getPatternRecognition() {
        try {
            return wekaService.patternRecognition();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing pattern recognition logic");
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> generateRandomNumbers() {
        return wekaService.generateRandom();
    }

    @GetMapping("/ensemble")
    public ResponseEntity<?> getEnsemblePrediction() {
        try {
            return wekaService.ensembleLottoPredictionLogic();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing ensemble prediction logic");
        }
    }

    @GetMapping("/montecarlo")
    public ResponseEntity<?> runMonteCarloSimulation() {
        return wekaService.monteCarloSimulation();
    }
}