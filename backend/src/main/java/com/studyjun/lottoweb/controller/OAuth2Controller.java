package com.studyjun.lottoweb.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/oauth2")
public class OAuth2Controller {

    @GetMapping("/code/{registrationId}")
    public String redirect(@PathVariable String registrationId, @AuthenticationPrincipal OAuth2User oauth2User) {
        // 사용자 정보 처리
        System.out.println("OAuth2 User Attributes: " + oauth2User.getAttributes());

        // 리디렉션 후 처리할 로직 추가
        return "OAuth2 login successful for: " + registrationId;  // 성공 메시지 반환
    }
}