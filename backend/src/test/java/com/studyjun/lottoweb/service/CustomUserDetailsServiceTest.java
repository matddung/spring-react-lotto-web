package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.CommonErrorCode;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @DisplayName("loadUserByUsername: 이메일로 유저를 정상 조회한다")
    @Test
    void loadUserByUsername_success() {
        User user = user(1L, "USER", "a@test.com");
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("a@test.com");

        assertThat(userDetails).isInstanceOf(UserPrincipal.class);
        assertThat(userDetails.getUsername()).isEqualTo("a@test.com");
    }

    @DisplayName("loadUserById: id로 유저를 정상 조회한다")
    @Test
    void loadUserById_success() {
        User user = user(2L, "ADMIN", "b@test.com");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserById(2L);

        assertThat(userDetails.getUsername()).isEqualTo("b@test.com");
    }

    @DisplayName("loadUserByUsername: 권한 정보가 principal에 포함된다")
    @Test
    void loadUserByUsername_containsAuthority() {
        User user = user(3L, "ADMIN", "c@test.com");
        when(userRepository.findByEmail("c@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("c@test.com");

        assertThat(userDetails.getAuthorities()).extracting("authority").contains("ADMIN");
    }

    @DisplayName("loadUserByUsername: 유저가 없으면 UsernameNotFoundException이 발생한다")
    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("none@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("유저 정보를 찾을 수 없습니다.");
    }

    @DisplayName("loadUserById: 유저가 없으면 BusinessException(RESOURCE_NOT_FOUND)이 발생한다")
    @Test
    void loadUserById_notFound_throwsBusinessException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserById(99L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(CommonErrorCode.RESOURCE_NOT_FOUND);
                });
    }

    private User user(Long id, String role, String email) {
        User user = User.builder()
                .email(email)
                .password("encoded")
                .nickname("nick" + id)
                .role(role)
                .providerId("local")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}