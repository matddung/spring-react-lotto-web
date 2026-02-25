package com.studyjun.lottoweb.handler;

import com.studyjun.lottoweb.util.CustomCookie;
import com.studyjun.lottoweb.repository.CustomAuthorizationRequestRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.studyjun.lottoweb.repository.CustomAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSimpleUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        try {
            log.error("OAuth2 로그인 실패가 발생했습니다. message={}", exception.getMessage(), exception);

            String targetUrl = CustomCookie.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue)
                    .orElse(("/"));

            targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", exception.getLocalizedMessage())
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUriString();

            customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception ex) {
            log.error("OAuth2 로그인 실패 후처리 중 예외가 발생했습니다.", ex);
            throw ex;
        }
    }
}