package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.TokenDto;
import com.studyjun.lottoweb.security.OAuth2Config;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomTokenProviderServiceTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    private CustomTokenProviderService tokenProviderService;

    @BeforeEach
    void setUp() {
        tokenProviderService = new CustomTokenProviderService();

        OAuth2Config.Auth auth = new OAuth2Config.Auth();
        ReflectionTestUtils.setField(
                auth,
                "tokenSecret",
                "0123456789012345678901234567890123456789012345678901234567890123"
        );
        ReflectionTestUtils.setField(auth, "accessTokenExpirationMsec", 60000L);
        ReflectionTestUtils.setField(auth, "refreshTokenExpirationMsec", 120000L);

        OAuth2Config.OAuth2 oauth2 = new OAuth2Config.OAuth2();
        OAuth2Config.OAuth2ConfigHolder holder = new OAuth2Config.OAuth2ConfigHolder(auth, oauth2);

        ReflectionTestUtils.setField(tokenProviderService, "oAuth2Config", holder);
        ReflectionTestUtils.setField(tokenProviderService, "customUserDetailsService", customUserDetailsService);
        tokenProviderService.init();
    }

    @DisplayName("createToken: access/refresh token을 정상 생성한다")
    @Test
    void createToken_success() {
        Authentication authentication = authentication();

        TokenDto tokenDto = tokenProviderService.createToken(authentication);

        assertThat(tokenDto.getAccessToken()).isNotBlank();
        assertThat(tokenDto.getRefreshToken()).isNotBlank();
        assertThat(tokenDto.getUserEmail()).isEqualTo("user@test.com");
    }

    @DisplayName("refreshToken: refresh token 유지한 access token 재발급이 가능하다")
    @Test
    void refreshToken_success() {
        Authentication authentication = authentication();

        TokenDto created = tokenProviderService.createToken(authentication);
        TokenDto refreshed = tokenProviderService.refreshToken(authentication, created.getRefreshToken());

        assertThat(refreshed.getRefreshToken()).isEqualTo(created.getRefreshToken());
        assertThat(refreshed.getAccessToken()).isNotBlank();
    }

    @DisplayName("getAuthenticationByEmail: 이메일 기반 인증 객체를 생성한다")
    @Test
    void getAuthenticationByEmail_success() {
        UserDetails userDetails = (UserDetails) authentication().getPrincipal();
        when(customUserDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        UsernamePasswordAuthenticationToken authentication = tokenProviderService.getAuthenticationByEmail("user@test.com");

        assertThat(authentication.getName()).isEqualTo("user@test.com");
    }


    @DisplayName("getAuthenticationById: access token으로 인증 객체를 생성한다")
    @Test
    void getAuthenticationById_success() {
        Authentication source = authentication();
        TokenDto tokenDto = tokenProviderService.createToken(source);
        UserDetails userDetails = (UserDetails) source.getPrincipal();
        when(customUserDetailsService.loadUserById(1L)).thenReturn(userDetails);

        UsernamePasswordAuthenticationToken authentication = tokenProviderService.getAuthenticationById(tokenDto.getAccessToken());

        assertThat(authentication.getName()).isEqualTo("user@test.com");
    }

    @DisplayName("validateToken: 잘못된 토큰은 false를 반환한다")
    @Test
    void validateToken_invalid_returnsFalse() {
        boolean valid = tokenProviderService.validateToken("invalid.token.value");
        assertThat(valid).isFalse();
    }

    @DisplayName("getUserIdFromToken: access token에서 사용자 ID를 추출한다")
    @Test
    void getUserIdFromToken_success() {
        Authentication authentication = authentication();
        TokenDto tokenDto = tokenProviderService.createToken(authentication);

        Long userId = tokenProviderService.getUserIdFromToken(tokenDto.getAccessToken());

        assertThat(userId).isEqualTo(1L);
    }

    private Authentication authentication() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "user@test.com",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }
}