package com.studyjun.lottoweb.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LottoResponse {
    @Schema( type = "int", example = "1" , description="1번째 로또 번호")
    private int drwtNo1;

    @Schema( type = "int", example = "2" , description="2번째 로또 번호")
    private int drwtNo2;

    @Schema( type = "int", example = "3" , description="3번째 로또 번호")
    private int drwtNo3;

    @Schema( type = "int", example = "4" , description="4번째 로또 번호")
    private int drwtNo4;

    @Schema( type = "int", example = "5" , description="5번째 로또 번호")
    private int drwtNo5;

    @Schema( type = "int", example = "6" , description="6번째 로또 번호")
    private int drwtNo6;

    @Schema( type = "int", example = "7" , description="보너스 로또 번호")
    private int bnusNo;

    @Schema( type = "string", example = "2024-08-14" , description="로또 날짜")
    private String drwNoDate;

    @Schema( type = "string", example = "success" , description="데이터 출력 성공 여부")
    private String returnValue;

    public int getFormattedDrawDate() {
        return Integer.parseInt(drwNoDate.replace("-", ""));
    }
}
