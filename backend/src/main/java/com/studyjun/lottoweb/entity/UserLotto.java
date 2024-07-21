package com.studyjun.lottoweb.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserLotto {
    @Id
    private String userEmail;

    private String top6Frequencies = "";
    private String patternRecognition = "";
    private String generateRandom = "";
    private String ensembleLottoPrediction = "";
    private String monteCarloSimulation = "";

    @Builder
    public UserLotto(
            String userEmail,
            String top6Frequencies,
            String patternRecognition,
            String generateRandom,
            String ensembleLottoPrediction,
            String monteCarloSimulation
    ) {
        this.userEmail = userEmail;
        this.top6Frequencies = top6Frequencies;
        this.patternRecognition = patternRecognition;
        this.generateRandom = generateRandom;
        this.ensembleLottoPrediction = ensembleLottoPrediction;
        this.monteCarloSimulation = monteCarloSimulation;
    }
}