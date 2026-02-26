package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.LottoResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.UserLotto;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.UserLottoRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WekaService {
    private Instances data;
    private final MultilayerPerceptron mlp = new MultilayerPerceptron();
    private final Bagging bagging = new Bagging();
    private final Random random;

    private static final String ARFF_FILE_PATH = "lotto.arff";
    private static final int NUMBER_OF_LOTTO_NUMBERS = 6;
    private static final int MAX_LOTTO_NUMBER = 45;

    @Autowired
    private UserLottoRepository userLottoRepository;

    @Autowired
    private UserRepository userRepository;

    public WekaService() throws Exception {
        try {
            DataSource source = new DataSource(getClass().getClassLoader().getResourceAsStream(ARFF_FILE_PATH));
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

    // 통계적 접근을 통해 나온 값으로 상위 6개 추출
    public ResponseEntity<?> top6Frequencies(UserPrincipal userPrincipal) {
        HashMap<Integer, Integer> frequencyMap = calculateFrequencies();
        List<Integer> top6Numbers = frequencyMap.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(6)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String top6NumbersString = top6Numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        UserLotto userLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername())
                .orElseGet(() -> {
                    UserLotto newUserLotto = new UserLotto();
                    newUserLotto.setUserEmail(userPrincipal.getUsername());
                    return newUserLotto;
                });

        if (userLotto.getTop6Frequencies().isEmpty()) {
            userLotto.setTop6Frequencies(top6NumbersString);
            userLottoRepository.save(userLotto);
            return ResponseEntity.ok(ApiResponse.success(top6Numbers));
        } else {
            ApiResponse apiResponse = ApiResponse.success(Message.builder().message("금주의 번호를 이미 받아보셨습니다.").build());
            return ResponseEntity.ok(apiResponse);
        }
    }

    // 패턴 인식 - Weka의 분류기를 사용하여 패턴을 인식
    public ResponseEntity<?> patternRecognition(String date, UserPrincipal userPrincipal) {
        try {
            double[] instanceValue = new double[data.numAttributes()];

            instanceValue[data.numAttributes() - 1] = Double.parseDouble(date.replaceAll("-", ""));

            for (int i = 0; i < data.numAttributes() - 1; i++) {
                instanceValue[i] = 0.0;
            }

            // 데이터 정규화
            Normalize normalize = new Normalize();
            normalize.setInputFormat(data);
            Instances normalizedData = Filter.useFilter(data, normalize);

            Instance newInstance = new DenseInstance(1.0, instanceValue);
            newInstance.setDataset(normalizedData);

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

            String numbersString = Arrays.stream(predictedNumbers)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(" "));

            UserLotto userLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername())
                    .orElseGet(() -> {
                        UserLotto newUserLotto = new UserLotto();
                        newUserLotto.setUserEmail(userPrincipal.getUsername());
                        return newUserLotto;
                    });

            if (userLotto.getPatternRecognition().isEmpty()) {
                userLotto.setPatternRecognition(numbersString);
                userLottoRepository.save(userLotto);
                return ResponseEntity.ok(ApiResponse.success(predictedNumbers));
            } else {
                ApiResponse apiResponse = ApiResponse.success(Message.builder().message("금주의 번호를 이미 받아보셨습니다.").build());
                return ResponseEntity.ok(apiResponse);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "패턴 인식 로직 처리 중 오류가 발생했습니다.");
        }
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
    public ResponseEntity<?> generateRandom(UserPrincipal userPrincipal) {
        Set<Integer> lottoNumbers = new HashSet<>();

        while (lottoNumbers.size() < NUMBER_OF_LOTTO_NUMBERS) {
            int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
            lottoNumbers.add(randomNum);
        }

        String numbersString = lottoNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        UserLotto userLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername())
                .orElseGet(() -> {
                    UserLotto newUserLotto = new UserLotto();
                    newUserLotto.setUserEmail(userPrincipal.getUsername());
                    return newUserLotto;
                });

        if (userLotto.getGenerateRandom().isEmpty()) {
            userLotto.setGenerateRandom(numbersString);
            userLottoRepository.save(userLotto);
            return ResponseEntity.ok(ApiResponse.success(lottoNumbers));
        } else {
            ApiResponse apiResponse = ApiResponse.success(Message.builder().message("금주의 번호를 이미 받아보셨습니다.").build());
            return ResponseEntity.ok(apiResponse);
        }
    }

    // 합의 알고리즘 - 앙상블 학습을 사용
    public ResponseEntity<?> ensembleLottoPrediction(String date, UserPrincipal userPrincipal) {
        try {
            double[] instanceValue = new double[data.numAttributes()];

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

            String numbersString = sortedLottoNumbers.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "));

            UserLotto userLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername())
                    .orElseGet(() -> {
                        UserLotto newUserLotto = new UserLotto();
                        newUserLotto.setUserEmail(userPrincipal.getUsername());
                        return newUserLotto;
                    });

            if (userLotto.getEnsembleLottoPrediction().isEmpty()) {
                userLotto.setEnsembleLottoPrediction(numbersString);
                userLottoRepository.save(userLotto);
                return ResponseEntity.ok(ApiResponse.success(sortedLottoNumbers));
            } else {
                ApiResponse apiResponse = ApiResponse.success(Message.builder().message("금주의 번호를 이미 받아보셨습니다.").build());
                return ResponseEntity.ok(apiResponse);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "앙상블 예측 로직 처리 중 오류가 발생했습니다.");
        }
    }

    // 시뮬레이션 - 몬테카를로 시뮬레이션을 통합
    public ResponseEntity<?> monteCarloSimulation(UserPrincipal userPrincipal) {
        int simulations = 10000;
        int[] counts = new int[MAX_LOTTO_NUMBER + 1];

        Set<Integer> lottoNumbers = new HashSet<>();

        while (lottoNumbers.size() < NUMBER_OF_LOTTO_NUMBERS) {
            int randomNum = random.nextInt(MAX_LOTTO_NUMBER) + 1;
            lottoNumbers.add(randomNum);
        }

        Random random = new Random();

        for (int i = 0; i < simulations; i++) {
            for (int num : lottoNumbers) {
                counts[num]++;
            }
        }

        List<Integer> topNumbers = IntStream.range(1, counts.length)
                .boxed()
                .sorted((i, j) -> Integer.compare(counts[j], counts[i]))
                .limit(NUMBER_OF_LOTTO_NUMBERS)
                .sorted()
                .toList();

        String numbersString = topNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        UserLotto userLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername())
                .orElseGet(() -> {
                    UserLotto newUserLotto = new UserLotto();
                    newUserLotto.setUserEmail(userPrincipal.getUsername());
                    return newUserLotto;
                });

        if (userLotto.getMonteCarloSimulation().isEmpty()) {
            userLotto.setMonteCarloSimulation(numbersString);
            userLottoRepository.save(userLotto);
            return ResponseEntity.ok(ApiResponse.success(topNumbers));
        } else {
            ApiResponse apiResponse = ApiResponse.success(Message.builder().message("금주의 번호를 이미 받아보셨습니다.").build());
            return ResponseEntity.ok(apiResponse);
        }
    }

    public ResponseEntity<?> getCurrentUserLottoInfo(UserPrincipal userPrincipal) {
        Optional<UserLotto> optionalUserLotto = userLottoRepository.findByUserEmail(userPrincipal.getUsername());

        if (optionalUserLotto.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(optionalUserLotto.get()));
        } else {
            UserLotto userLotto = new UserLotto();
            userLotto.setUserEmail(userPrincipal.getUsername());
            userLottoRepository.save(userLotto);
            return ResponseEntity.ok(ApiResponse.success(userLotto));
        }
    }

    public void updateArffFileFromStartDraw(LottoUpdateService lottoUpdateService) throws Exception {
        int latestDrawNo = lottoUpdateService.getLatestDrawNo();

        while (true) {
            LottoResponse lottoResponse = lottoUpdateService.getLottoInfoByDrawNumber(latestDrawNo);

            if (lottoResponse == null || !"success".equals(lottoResponse.getReturnValue())) {
                break;
            }

            boolean updated = updateArffFile(lottoResponse);
            lottoUpdateService.saveLatestDrawNo(latestDrawNo);

            latestDrawNo++;
        }
    }

    public boolean updateArffFile(LottoResponse lottoResponse) throws Exception {
        if (!"success".equals(lottoResponse.getReturnValue())) {
            return false;
        }

        DataSource source = new DataSource(ARFF_FILE_PATH);
        Instances data = source.getDataSet();

        Attribute drawDateAttribute = data.attribute("drwNoDate");

        if (drawDateAttribute == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "ARFF 파일에 drwNoDate 속성이 없습니다.");
        }

        boolean isDuplicate = false;
        int formattedDrawDate = lottoResponse.getFormattedDrawDate();
        for (Instance instance : data) {
            if ((int) instance.value(drawDateAttribute) == formattedDrawDate) {
                isDuplicate = true;
                break;
            }
        }

        if (!isDuplicate) {
            double[] instanceValues = new double[data.numAttributes()];
            instanceValues[0] = lottoResponse.getDrwtNo1();
            instanceValues[1] = lottoResponse.getDrwtNo2();
            instanceValues[2] = lottoResponse.getDrwtNo3();
            instanceValues[3] = lottoResponse.getDrwtNo4();
            instanceValues[4] = lottoResponse.getDrwtNo5();
            instanceValues[5] = lottoResponse.getDrwtNo6();
            instanceValues[6] = lottoResponse.getBnusNo();
            instanceValues[7] = formattedDrawDate;

            Instance newInstance = new DenseInstance(1.0, instanceValues);
            data.add(newInstance);

            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File("src/main/resources/" + ARFF_FILE_PATH));
            saver.writeBatch();

            return true;
        } else {
            return false;
        }
    }
}