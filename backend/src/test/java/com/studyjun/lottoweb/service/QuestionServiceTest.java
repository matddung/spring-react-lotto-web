package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.CreateAnswerRequest;
import com.studyjun.lottoweb.dto.request.CreateQuestionRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.AnswerRepository;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QuestionService questionService;

    @DisplayName("createQuestion: 정상적으로 질문을 등록한다")
    @Test
    void createQuestion_success() {
        User user = user(1L, "USER");
        UserPrincipal userPrincipal = userPrincipal(1L);
        CreateQuestionRequest request = new CreateQuestionRequest();
        request.setSubject("제목");
        request.setContent("내용");
        request.setIsPrivate(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = questionService.createQuestion(userPrincipal, request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        Message message = (Message) body.getData();
        assertThat(message.getMessage()).isEqualTo("고객센터에 질문이 등록되었습니다.");
        verify(questionRepository).save(any(Question.class));
    }

    @DisplayName("showQuestionDetail: 공개글은 정상 조회된다")
    @Test
    void showQuestionDetail_publicQuestion_success() {
        User author = user(2L, "USER");
        User viewer = user(1L, "USER");
        Question question = Question.builder()
                .subject("제목")
                .content("내용")
                .author(author)
                .isPrivate(false)
                .build();

        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));

        ResponseEntity<?> response = questionService.showQuestionDetail(10L, userPrincipal(1L));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        assertThat(body.isCheck()).isTrue();
        assertThat(body.getData()).isEqualTo(question);
    }

    @DisplayName("createAnswer: 관리자 사용자는 답변 등록에 성공한다")
    @Test
    void createAnswer_admin_success() {
        User admin = user(99L, "ADMIN");
        Question question = Question.builder()
                .subject("질문")
                .content("질문내용")
                .author(user(1L, "USER"))
                .isPrivate(false)
                .build();
        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setSubject("답변");
        request.setContent("답변내용");

        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(questionRepository.findById(55L)).thenReturn(Optional.of(question));

        ResponseEntity<?> response = questionService.createAnswer(55L, userPrincipal(99L), request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        ApiResponse body = (ApiResponse) response.getBody();
        Message message = (Message) body.getData();
        assertThat(message.getMessage()).isEqualTo("답변이 등록되었습니다.");
        verify(answerRepository).save(any());
    }

    @DisplayName("showQuestionDetail: 비공개글을 타인이 조회하면 BusinessException(FORBIDDEN)이 발생한다")
    @Test
    void showQuestionDetail_privateQuestion_forbidden() {
        User author = user(2L, "USER");
        User viewer = user(1L, "USER");
        Question question = Question.builder()
                .subject("비밀")
                .content("비밀내용")
                .author(author)
                .isPrivate(true)
                .build();

        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));

        assertThatThrownBy(() -> questionService.showQuestionDetail(10L, userPrincipal(1L)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                    assertThat(be.getMessage()).isEqualTo("비밀글입니다.");
                });
    }

    @DisplayName("createAnswer: 관리자 권한이 없으면 BusinessException(INVALID_INPUT_VALUE)이 발생한다")
    @Test
    void createAnswer_nonAdmin_throwsBusinessException() {
        User normalUser = user(1L, "USER");
        CreateAnswerRequest request = new CreateAnswerRequest();
        request.setSubject("답변");
        request.setContent("답변내용");

        when(userRepository.findById(1L)).thenReturn(Optional.of(normalUser));

        assertThatThrownBy(() -> questionService.createAnswer(55L, userPrincipal(1L), request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
                    assertThat(be.getMessage()).isEqualTo("관리자 권한이 없습니다.");
                });
    }

    private User user(Long id, String role) {
        User user = User.builder()
                .email("user" + id + "@test.com")
                .password("encoded")
                .nickname("user" + id)
                .role(role)
                .providerId("local")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private UserPrincipal userPrincipal(Long id) {
        return new UserPrincipal(id, "user" + id + "@test.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}