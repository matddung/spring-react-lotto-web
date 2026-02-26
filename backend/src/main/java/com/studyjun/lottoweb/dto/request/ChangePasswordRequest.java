package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @Schema( type = "string", example = "string", description="기존 비밀번호 입니다.")
    @NotBlank(message = "기존 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 50, message = "기존 비밀번호는 8자 이상 50자 이하로 입력해 주세요.")
    private String oldPassword;

    @Schema( type = "string", example = "string123", description="신규 비밀번호 입니다.")
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Size(min = 8, max = 50, message = "새 비밀번호는 8자 이상 50자 이하로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "새 비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해 주세요.")
    private String newPassword;

    @Schema( type = "string", example = "string123", description="신규 비밀번호 확인란 입니다.")
    @NotBlank(message = "새 비밀번호 확인 값을 입력해 주세요.")
    @Size(min = 8, max = 50, message = "새 비밀번호 확인 값은 8자 이상 50자 이하로 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "새 비밀번호 확인 값은 영문, 숫자, 특수문자를 각각 1개 이상 포함해 주세요.")
    private String reNewPassword;
}