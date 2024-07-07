package com.studyjun.lottoweb.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WekaService {
    private Instances data;
    private final MultilayerPerceptron mlp = new MultilayerPerceptron();
    private final Bagging bagging = new Bagging();
    private final Random random;

    private static final int NUMBER_OF_LOTTO_NUMBERS = 6;
    private static final int MAX_LOTTO_NUMBER = 45;

    public WekaService() throws Exception {
        try {
            DataSource source = new DataSource(getClass().getClassLoader().getResourceAsStream("lotto.arff"));
            data = source.getDataSet();
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mlp.setHiddenLayers("5");
        mlp.setLearningRate(0.1);
        mlp.setMomentum(0.2);
        mlp.setTrainingTime(500);
        mlp.buildClassifier(data);

        bagging.setClassifier(new RandomForest());
        bagging.setNumIterations(10);
        bagging.buildClassifier(data);

        random = new Random();
    }

    private HashMap<Integer, Integer> calculateFrequencies() {
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (int i = 0; i < data.numInstances(); i++) {
            for (int j = 0; j < data.numAttributes() - 1; j++) {
                int number = (int) data.instance(i).value(j);
                if (number >= 1 && number <= 45) {
                    frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
                }
            }
        }
        return frequencyMap;
    }

    // 통계적 접근 - Weka를 사용하여 특정 번호의 빈도를 분석
    public ResponseEntity<?> statistics() {
        HashMap<Integer, Integer> frequencyMap = calculateFrequencies();
        return ResponseEntity.ok(frequencyMap);
    }

    // 통계적 접근을 통해 나온 값으로 상위 6개 추출
    public ResponseEntity<?> top6Frequencies() {
        HashMap<Integer, Integer> frequencyMap = calculateFrequencies();
        List<Integer> top6Numbers = frequencyMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return ResponseEntity.ok(top6Numbers);
    }

    // 패턴 인식 - Weka의 분류기를 사용하여 패턴을 인식
    public ResponseEntity<?> patternRecognition() throws Exception {
        double[] instanceValue = new double[data.numAttributes()];

        String date = "2024-07-12";
        instanceValue[data.numAttributes() - 1] = Double.parseDouble(date.replaceAll("-", ""));

        for (int i = 0; i < data.numAttributes() - 1; i++) {
            instanceValue[i] = 0.0;
        }

        Instance newInstance = new DenseInstance(1.0, instanceValue);
        newInstance.setDataset(data);

        double[] predictedValues = mlp.distributionForInstance(newInstance);

        double minPredictedValue = Arrays.stream(predictedValues).min().orElse(0.0);
        double maxPredictedValue = Arrays.stream(predictedValues).max().orElse(1.0);

        int[] predictedNumbers;
        if (minPredictedValue == maxPredictedValue) {
            // 예측 값이 동일한 경우 기본적인 분포를 기반으로 숫자를 생성
            predictedNumbers = generateFallbackLottoNumbers();
        } else {
            predictedNumbers = Arrays.stream(predictedValues)
                    .map(d -> 1 + ((d - minPredictedValue) / (maxPredictedValue - minPredictedValue)) * (MAX_LOTTO_NUMBER - 1))
                    .mapToInt(d -> (int) Math.round(d))
                    .filter(num -> num >= 1 && num <= MAX_LOTTO_NUMBER)
                    .distinct()
                    .limit(NUMBER_OF_LOTTO_NUMBERS)
                    .sorted()
                    .toArray();

            // 필요한 숫자의 개수가 부족할 경우 추가 숫자를 무작위로 채움
            Set<Integer> numberSet = new HashSet<>();
            for (int num : predictedNumbers) {
                numberSet.add(num);
            }

            while (numberSet.size() < NUMBER_OF_LOTTO_NUMBERS) {
                int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
                numberSet.add(randomNum);
            }

            predictedNumbers = numberSet.stream().mapToInt(Integer::intValue).sorted().toArray();
        }

        return ResponseEntity.ok(predictedNumbers);
    }

    private int[] generateFallbackLottoNumbers() {
        Set<Integer> lottoNumbers = new HashSet<>();

        while (lottoNumbers.size() < NUMBER_OF_LOTTO_NUMBERS) {
            int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
            lottoNumbers.add(randomNum);
        }

        return lottoNumbers.stream().mapToInt(Integer::intValue).sorted().toArray();
    }

    // 무작위 샘플링 - 무작위 숫자 생성
    public ResponseEntity<?> generateRandom() {
        Set<Integer> lottoNumbers = new HashSet<>();

        while (lottoNumbers.size() < NUMBER_OF_LOTTO_NUMBERS) {
            int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
            lottoNumbers.add(randomNum);
        }
        return ResponseEntity.ok(lottoNumbers);
    }

    // 합의 알고리즘 - 앙상블 학습을 사용
    public ResponseEntity<?> ensembleLottoPredictionLogic() throws Exception {
        double[] instanceValue = new double[data.numAttributes()];

        String date = "2024-07-12";
        instanceValue[data.numAttributes() - 1] = Double.parseDouble(date.replaceAll("-", ""));

        Instance newInstance = new DenseInstance(1.0, instanceValue);
        newInstance.setDataset(data);

        double[] predictedValues = bagging.distributionForInstance(newInstance);

        int[] predictedNumbers = Arrays.stream(predictedValues)
                .mapToInt(d -> (int) Math.round(d))
                .filter(num -> num >= 1 && num <= 45)
                .distinct()
                .limit(NUMBER_OF_LOTTO_NUMBERS)
                .toArray();

        Set<Integer> predictedNumberSet = new HashSet<>();

        for (int num : predictedNumbers) {
            predictedNumberSet.add(num);
        }

        while (predictedNumberSet.size() < NUMBER_OF_LOTTO_NUMBERS) {
            int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
            predictedNumberSet.add(randomNum);
        }

        List<Integer> sortedLottoNumbers = predictedNumberSet.stream()
                .sorted()
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedLottoNumbers);
    }

    // 시뮬레이션 - 몬테카를로 시뮬레이션을 통합
    public ResponseEntity<?> monteCarloSimulation() {
        int simulations = 10000;
        int[] counts = new int[MAX_LOTTO_NUMBER + 1];

        Random random = new Random();
        for (int i = 0; i < simulations; i++) {
            Set<Integer> numbers = (Set<Integer>) generateRandom().getBody();
            assert numbers != null;
            for (int num : numbers) {
                counts[num]++;
            }
        }

        List<Integer> topNumbers = IntStream.range(1, counts.length)
                .boxed()
                .sorted((i, j) -> Integer.compare(counts[j], counts[i]))
                .limit(NUMBER_OF_LOTTO_NUMBERS)
                .toList();

        List<Integer> sortedLottoNumbers = topNumbers.stream()
                .sorted()
                .toList();

        return ResponseEntity.ok(sortedLottoNumbers);
    }
}