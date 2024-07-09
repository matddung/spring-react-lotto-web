package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangeNicknameRequest {
    @Schema( type = "string", example = "string1234", description="새 닉네임 입니다.")
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9가-힣]+$", message = "닉네임은 특수 문자를 포함할 수 없습니다.")
    private String newNickname;
}
