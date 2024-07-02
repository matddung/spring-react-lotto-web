package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeNicknameRequest {
    @Schema( type = "string", example = "string1234", description="새 닉네임 입니다.")
    @NotBlank
    @NotNull
    private String newNickname;
}
