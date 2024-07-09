package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateQuestionRequest {
    @Schema( type = "string", example = "포인트 충전 문의", description="고객센터 질문 제목입니다.")
    @NotBlank
    @NotNull
    private String subject;

    @Schema( type = "string", example = "포인트 충전하려는데 어떻게 하나요?", description="고객센터 질문 내용입니다.")
    @NotBlank
    @NotNull
    private String content;

    @Schema( type = "boolean", example = "true", description="비밀글 여부를 체크합니다.")
    @NotBlank
    @NotNull
    private Boolean isPrivate;
}