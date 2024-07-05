package com.studyjun.lottoweb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    
    @Schema( type = "string", example = "string", description="계정 명 입니다.")
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력하세요.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]+$", message = "닉네임은 특수 문자를 포함할 수 없습니다.")
    @NotBlank
    private String name;

    @Schema( type = "string", example = "string@aa.bb", description="계정 이메일 입니다.")
    @NotBlank
    @Email
    private String email;

    @Schema( type = "string", example = "string", description="계정 비밀번호 입니다.")
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 8자 이상이어야 하며, 숫자와 특수 문자를 포함해야 합니다.")
    private String password;
}