package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAnswerRequest {
    @Schema( type = "string", example = "포인트 충전 문의 : 답변입니다.", description="고객센터 답변 제목입니다.")
    @NotBlank
    @NotNull
    private String subject;

    @Schema( type = "string", example = "포인트 충전하려는데 어떻게 하나요? : 잘하세요.", description="고객센터 답변 내용입니다.")
    @NotBlank
    @NotNull
    private String content;
}
