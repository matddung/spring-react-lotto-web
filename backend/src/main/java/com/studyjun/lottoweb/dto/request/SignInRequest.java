package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "올바른 이메일 형식으로 입력해 주세요.")
    private String email;

    @Schema( type = "string", example = "string", description="계정 비밀번호 입니다.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하로 입력해 주세요.")
    private String password;
}