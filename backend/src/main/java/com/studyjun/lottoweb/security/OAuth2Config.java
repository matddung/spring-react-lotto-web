package com.studyjun.lottoweb.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OAuth2Config {

    @Component
    public static class Auth {
        @Value("${jwt.secret-key}")
        private String tokenSecret;

        @Value("${jwt.access.expiration}")
        private long accessTokenExpirationMsec;

        @Value("${jwt.refresh.expiration}")
        private long refreshTokenExpirationMsec;

        @PostConstruct
        public void init() {
            if (tokenSecret == null) {
                throw new IllegalArgumentException("Token secret cannot be null.");
            }
            if (accessTokenExpirationMsec == 0) {
                throw new IllegalArgumentException("Access token expiration cannot be zero.");
            }
            if (refreshTokenExpirationMsec == 0) {
                throw new IllegalArgumentException("Refresh token expiration cannot be zero.");
            }
        }

        // Getters
        public String getTokenSecret() {
            return tokenSecret;
        }

        public long getAccessTokenExpirationMsec() {
            return accessTokenExpirationMsec;
        }

        public long getRefreshTokenExpirationMsec() {
            return refreshTokenExpirationMsec;
        }
    }

    @Component
    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        @Value("${app.oauth2.authorizedRedirectUris}")
        public void setAuthorizedRedirectUris(String authorizedRedirectUris) {
            this.authorizedRedirectUris = Arrays.asList(authorizedRedirectUris.split(","));
        }

        // Getter
        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }
    }

    @Component
    public static class OAuth2ConfigHolder {
        private final Auth auth;
        private final OAuth2 oauth2;

        public OAuth2ConfigHolder(Auth auth, OAuth2 oauth2) {
            this.auth = auth;
            this.oauth2 = oauth2;
        }

        public Auth getAuth() {
            return auth;
        }

        public OAuth2 getOauth2() {
            return oauth2;
        }
    }
}