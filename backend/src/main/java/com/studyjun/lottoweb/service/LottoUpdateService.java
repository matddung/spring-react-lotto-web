package com.studyjun.lottoweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyjun.lottoweb.dto.response.LottoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LottoUpdateService {

    private static final String LOTTO_API_URL = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=";
    private static final String LATEST_DRAW_NO_FILE = "latestDrawNo.txt";

    private String getResourceFilePath() throws IOException {
        Path path = Paths.get("src/main/resources/" + LATEST_DRAW_NO_FILE);
        File file = new File(path.toString());
        if (!file.exists()) {
            if (file.createNewFile()) {
                System.out.println("Created new file: " + file.getAbsolutePath());
            } else {
                throw new IOException("Failed to create file: " + file.getAbsolutePath());
            }
        }
        return file.getAbsolutePath();
    }

    public int getLatestDrawNo() throws IOException {
        String filePath = getResourceFilePath();
        File file = new File(filePath);

        if (!file.exists()) {
            FileWriter writer = new FileWriter(file);
            writer.write("1");
            writer.close();
            return 1;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String latestDrawNoStr = reader.readLine();
        reader.close();

        if (latestDrawNoStr == null || latestDrawNoStr.isEmpty()) {
            return 1;
        }

        return Integer.parseInt(latestDrawNoStr);
    }

    public void saveLatestDrawNo(int drawNo) throws IOException {
        String filePath = getResourceFilePath();
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(Integer.toString(drawNo));
            writer.flush();  // 즉시 데이터를 파일에 씁니다.
            System.out.println("Saved latest draw number: " + drawNo);
        }
    }

    public LottoResponse getLottoInfoByDrawNumber(int drawNo) {
        RestTemplate restTemplate = new RestTemplate();
        String url = LOTTO_API_URL + drawNo;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String responseBody = response.getBody();

            if (responseBody != null && responseBody.startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseBody, LottoResponse.class);
            } else {
                System.out.println("Unexpected response format: " + responseBody);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}