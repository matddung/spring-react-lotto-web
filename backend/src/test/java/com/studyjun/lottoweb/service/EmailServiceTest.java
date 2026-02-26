package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.FindPasswordRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private EmailService emailService;

    @DisplayName("getTempString: 8자리 임시 비밀번호를 생성한다")
    @Test
    void getTempString_success() {
        String temp = emailService.getTempString();
        assertThat(temp).hasSize(8).matches("^[0-9A-Z]{8}$");
    }

    @DisplayName("updatePassword: 사용자 비밀번호를 정상 변경한다")
    @Test
    void updatePassword_success() {
        User user = user(1L, "a@test.com");
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));
        when(encoder.encode("TEMP1234")).thenReturn("encodedTemp");

        emailService.updatePassword("TEMP1234", "a@test.com");

        verify(userRepository).save(user);
    }

    @DisplayName("sendTempPasswordMail: 임시 비밀번호 메일 발송에 성공한다")
    @Test
    void sendTempPasswordMail_success() throws Exception {
        User user = user(2L, "b@test.com");
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        FindPasswordRequest request = new FindPasswordRequest();
        request.setEmail("b@test.com");

        when(userRepository.existsByEmail("b@test.com")).thenReturn(true);
        when(userRepository.findByEmail("b@test.com")).thenReturn(Optional.of(user));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(encoder.encode(any(String.class))).thenReturn("encoded");

        Message response = emailService.sendTempPasswordMail(request);

        assertThat(response.getMessage()).isEqualTo("임시 비밀번호를 이메일로 발송했습니다.");
        verify(mailSender).send(any(MimeMessage.class));
    }

    @DisplayName("sendTempPasswordMail: 이메일이 없으면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void sendTempPasswordMail_emailNotFound_throwsBusinessException() {
        FindPasswordRequest request = new FindPasswordRequest();
        request.setEmail("none@test.com");
        when(userRepository.existsByEmail("none@test.com")).thenReturn(false);

        assertThatThrownBy(() -> emailService.sendTempPasswordMail(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("해당 이메일이 존재하지 않습니다.");
                });
    }

    @DisplayName("updatePassword: 사용자가 없으면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void updatePassword_userNotFound_throwsBusinessException() {
        when(userRepository.findByEmail("none@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailService.updatePassword("TEMP1234", "none@test.com"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
                });
    }

    private User user(Long id, String email) {
        User user = User.builder()
                .email(email)
                .password("old")
                .nickname("nick" + id)
                .role("USER")
                .providerId("local")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}