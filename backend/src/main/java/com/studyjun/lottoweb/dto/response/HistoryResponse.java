package com.studyjun.lottoweb.dto.response;

import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class HistoryResponse {
    @Schema( type = "string", example = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2NTI3OTgxOTh9.6CoxHB_siOuz6PxsxHYQCgUT1_QbdyKTUwStQDutEd1-cIIARbQ0cyrnAmpIgi3IBoLRaqK7N1vXO42nYy4g5g" , description="access token 을 출력합니다.")
    private User user;

    @Schema( type = "string", example = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2NTI3OTgxOTh9.asdf8as4df865as4dfasdf65_asdfweioufsdoiuf_432jdsaFEWFSDV_sadf" , description="refresh token 을 출력합니다.")
    private List<Question> question;

    @Builder
    public HistoryResponse(User user, List<Question> question) {
        this.user = user;
        this.question = question;
    }
}
