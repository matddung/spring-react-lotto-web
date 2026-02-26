package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.HistoryResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private AdminService adminService;

    @DisplayName("getAllUsers: 관리자 계정은 전체 유저를 정상 조회한다")
    @Test
    void getAllUsers_success() {
        User admin = user(1L, "ADMIN");
        Page<User> users = new PageImpl<>(List.of(user(2L, "USER")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(users);

        ResponseEntity<?> response = adminService.getAllUsers(principal(1L), 0);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        assertThat(body.isCheck()).isTrue();
        assertThat(body.getData()).isEqualTo(users);
    }

    @DisplayName("getUserHistory: 관리자 계정은 대상 유저 이력을 정상 조회한다")
    @Test
    void getUserHistory_success() {
        User admin = user(1L, "ADMIN");
        User target = user(2L, "USER");
        Page<Question> questions = new PageImpl<>(List.of(Question.builder().subject("s").content("c").author(target).isPrivate(false).build()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(questionRepository.findByAuthorIdOrderByCreatedDateDesc(any(org.springframework.data.domain.Pageable.class), org.mockito.ArgumentMatchers.eq(2L))).thenReturn(questions);

        ResponseEntity<?> response = adminService.getUserHistory(2L, principal(1L), 0);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        HistoryResponse history = (HistoryResponse) body.getData();
        assertThat(history.getUser()).isEqualTo(target);
        assertThat(history.getQuestion()).isEqualTo(questions);
    }

    @DisplayName("deleteUser: 관리자 계정은 유저 삭제를 정상 수행한다")
    @Test
    void deleteUser_success() {
        User admin = user(1L, "ADMIN");
        User target = user(2L, "USER");
        List<Question> questions = List.of(Question.builder().subject("s").content("c").author(target).isPrivate(false).build());

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(questionRepository.findByAuthorId(2L)).thenReturn(questions);

        ResponseEntity<?> response = adminService.deleteUser(2L, principal(1L));

        ApiResponse body = (ApiResponse) response.getBody();
        Message message = (Message) body.getData();
        assertThat(message.getMessage()).isEqualTo("유저 삭제가 완료되었습니다.");
        verify(questionRepository).deleteAll(questions);
        verify(userRepository).delete(target);
    }

    @DisplayName("isAdmin: 일반 사용자는 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void isAdmin_nonAdmin_throwsBusinessException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "USER")));

        assertThatThrownBy(() -> adminService.isAdmin(principal(2L)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("ADMIN 계정이 아닙니다.");
                });
    }

    @DisplayName("deleteUser: 대상 유저가 없으면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void deleteUser_userNotFound_throwsBusinessException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "ADMIN")));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deleteUser(99L, principal(1L)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
                });
    }

    private User user(Long id, String role) {
        User user = User.builder()
                .email("u" + id + "@t.com")
                .password("p")
                .nickname("n" + id)
                .role(role)
                .providerId("local")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private UserPrincipal principal(Long id) {
        return new UserPrincipal(id, "u" + id + "@t.com", "p", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}