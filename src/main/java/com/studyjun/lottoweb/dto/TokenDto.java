package com.studyjun.lottoweb.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TokenDto {
    private String userEmail;
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenDto(String userEmail, String accessToken, String refreshToken){
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
