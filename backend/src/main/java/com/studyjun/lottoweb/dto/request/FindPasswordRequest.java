package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FindPasswordRequest {
    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @NotBlank
    @NotNull
    private String email;
}
