package com.studyjun.lottoweb.handler;


import com.studyjun.lottoweb.util.CustomCookie;
import com.studyjun.lottoweb.util.DefaultAssert;
import com.studyjun.lottoweb.security.OAuth2Config;
import com.studyjun.lottoweb.dto.TokenDto;
import com.studyjun.lottoweb.entity.Token;
import com.studyjun.lottoweb.repository.CustomAuthorizationRequestRepository;
import com.studyjun.lottoweb.repository.TokenRepository;
import com.studyjun.lottoweb.service.CustomTokenProviderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.studyjun.lottoweb.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomTokenProviderService customTokenProviderService;
    private final OAuth2Config.OAuth2ConfigHolder oAuth2Config;
    private final TokenRepository tokenRepository;
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultAssert.isAuthentication(!response.isCommitted());

        try {
            String targetUrl = determineTargetUrl(request, response, authentication);

            clearAuthenticationAttributes(request, response);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception ex) {
            log.error("OAuth2 로그인 성공 후처리 중 예외가 발생했습니다.", ex);
            throw ex;
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        String targetUrl = resolveTargetUrl(redirectUri);

        TokenDto tokenDto = customTokenProviderService.createToken(authentication);
        Token token = Token.builder()
                .userEmail(tokenDto.getUserEmail())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        tokenRepository.save(token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokenDto.getAccessToken())
                .build().toUriString();
    }

    private String resolveTargetUrl(Optional<String> redirectUri) {
        if (redirectUri.isPresent()) {
            String uri = redirectUri.get();
            if (isAuthorizedRedirectUri(uri) || isTrustedLocalRedirectUri(uri)) {
                return uri;
            }
        }

        String fallbackTargetUrl = oAuth2Config.getOauth2().getAuthorizedRedirectUris().stream()
                .findFirst()
                .orElse("http://localhost:3000/oauth2/redirect");

        if (redirectUri.isPresent()) {
            log.warn("허용되지 않은 redirect_uri 입니다. fallbackUri={}, redirectUri={}", fallbackTargetUrl, redirectUri.get());
        }

        return fallbackTargetUrl;
    }

    private boolean isTrustedLocalRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return "localhost".equalsIgnoreCase(clientRedirectUri.getHost())
                && "/oauth2/redirect".equals(clientRedirectUri.getPath());
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return oAuth2Config.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}