package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Schema( type = "string", example = "string", description="기존 비밀번호 입니다.")
    @NotBlank
    @NotNull
    private String oldPassword;

    @Schema( type = "string", example = "string123", description="신규 비밀번호 입니다.")
    @NotBlank
    @NotNull
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 8자 이상이어야 하며, 숫자와 특수 문자를 포함해야 합니다.")
    private String newPassword;

    @Schema( type = "string", example = "string123", description="신규 비밀번호 확인란 입니다.")
    @NotBlank
    @NotNull
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 8자 이상이어야 하며, 숫자와 특수 문자를 포함해야 합니다.")
    private String reNewPassword;
}