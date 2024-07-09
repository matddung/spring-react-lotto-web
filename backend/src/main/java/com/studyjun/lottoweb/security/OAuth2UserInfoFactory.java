package com.studyjun.lottoweb.security;

import com.studyjun.lottoweb.config.Provider;
import com.studyjun.lottoweb.entity.oAuth2.Google;
import com.studyjun.lottoweb.entity.oAuth2.Kakao;
import com.studyjun.lottoweb.entity.oAuth2.Naver;
import com.studyjun.lottoweb.entity.oAuth2.OAuth2UserInfo;
import com.studyjun.lottoweb.util.DefaultAssert;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(Provider.google.toString())) {
            return new Google(attributes);
        } else if (registrationId.equalsIgnoreCase(Provider.naver.toString())) {
            return new Naver(attributes);
        } else if (registrationId.equalsIgnoreCase(Provider.kakao.toString())) {
            return new Kakao(attributes);
        } else {
            DefaultAssert.isAuthentication("해당 oauth2 기능은 지원하지 않습니다.");
        }
        return null;
    }
}
