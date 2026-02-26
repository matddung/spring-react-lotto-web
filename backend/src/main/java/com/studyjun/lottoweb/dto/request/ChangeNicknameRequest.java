package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeNicknameRequest {
    @Schema( type = "string", example = "string1234", description="새 닉네임 입니다.")
    @NotBlank(message = "새 닉네임을 입력해 주세요.")
    @Size(min = 2, max = 8, message = "새 닉네임은 2자 이상 8자 이하로 입력해 주세요.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]+$", message = "새 닉네임은 한글, 영문, 숫자만 사용할 수 있어요.")
    private String newNickname;
}
