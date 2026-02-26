package com.studyjun.lottoweb.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class CustomOncePerRequestFilterTest {

    private final CustomOncePerRequestFilter filter = new CustomOncePerRequestFilter();

    @DisplayName("refresh endpoint는 JWT 필터를 건너뛴다")
    @Test
    void shouldNotFilter_refreshEndpoint() throws ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/user/refresh");

        boolean result = filter.shouldNotFilter(request);

        assertThat(result).isTrue();
    }

    @DisplayName("일반 API는 JWT 필터를 통과한다")
    @Test
    void shouldFilter_otherEndpoints() throws ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/user/signIn");

        boolean result = filter.shouldNotFilter(request);

        assertThat(result).isFalse();
    }
}