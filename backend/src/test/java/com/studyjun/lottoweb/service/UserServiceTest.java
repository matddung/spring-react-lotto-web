package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.ChangeNicknameRequest;
import com.studyjun.lottoweb.dto.request.ChangePasswordRequest;
import com.studyjun.lottoweb.dto.request.SignUpRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.TokenRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomTokenProviderService customTokenProviderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private UserService userService;

    @DisplayName("signUp: 신규 사용자를 정상 등록한다")
    @Test
    void signUp_success() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("new@test.com");
        request.setName("newUser");
        request.setPassword("Passw0rd!");

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setScheme("http");
        servletRequest.setServerName("localhost");
        servletRequest.setServerPort(8080);
        servletRequest.setContextPath("");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));
        try {
            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(userRepository.existsByNickname("newUser")).thenReturn(false);
            when(passwordEncoder.encode("Passw0rd!")).thenReturn("encodedPwd");

            ResponseEntity<?> response = userService.signUp(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            ApiResponse body = (ApiResponse) response.getBody();
            Message message = (Message) body.getData();
            assertThat(message.getMessage()).isEqualTo("회원가입에 성공하였습니다.");
            verify(userRepository).save(any(User.class));
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @DisplayName("nicknameModify: 중복이 아니면 닉네임 변경에 성공한다")
    @Test
    void nicknameModify_success() {
        User user = user(1L, "USER");
        ChangeNicknameRequest request = new ChangeNicknameRequest();
        request.setNewNickname("newNick");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);

        ResponseEntity<?> response = userService.nicknameModify(userPrincipal(1L), request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        Message message = (Message) body.getData();
        assertThat(message.getMessage()).isEqualTo("닉네임 변경에 성공하였습니다.");
        verify(userRepository).save(user);
    }

    @DisplayName("passwordModify: 기존 비밀번호와 신규 확인값이 맞으면 변경 성공")
    @Test
    void passwordModify_success() {
        User user = user(1L, "USER");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPwd");
        request.setNewPassword("Newpass1!");
        request.setReNewPassword("Newpass1!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("Newpass1!")).thenReturn("encodedNewPwd");

        ResponseEntity<?> response = userService.passwordModify(userPrincipal(1L), request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        Message message = (Message) body.getData();
        assertThat(message.getMessage()).isEqualTo("비밀번호 변경에 성공하였습니다.");
        verify(userRepository).save(user);
    }

    @DisplayName("signUp: 이메일이 중복이면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void signUp_duplicateEmail_throwsBusinessException() {
        SignUpRequest request = new SignUpRequest();
        request.setEmail("dup@test.com");
        request.setName("newUser");
        request.setPassword("Passw0rd!");

        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("해당 이메일이 이미 존재합니다.");
                });
    }

    @DisplayName("nicknameModify: 닉네임이 중복이면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void nicknameModify_duplicateNickname_throwsBusinessException() {
        User user = user(1L, "USER");
        ChangeNicknameRequest request = new ChangeNicknameRequest();
        request.setNewNickname("taken");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.nicknameModify(userPrincipal(1L), request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("중복된 닉네임입니다.");
                });
    }

    private User user(Long id, String role) {
        User user = User.builder()
                .email("user" + id + "@test.com")
                .password("encodedOldPwd")
                .nickname("user" + id)
                .role(role)
                .providerId("local")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private UserPrincipal userPrincipal(Long id) {
        return new UserPrincipal(id, "user" + id + "@test.com", "encodedOldPwd",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}