package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.CreateAnswerRequest;
import com.studyjun.lottoweb.dto.request.CreateQuestionRequest;
import com.studyjun.lottoweb.dto.response.*;
import com.studyjun.lottoweb.entity.Answer;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.exception.AuthErrorCode;
import com.studyjun.lottoweb.exception.BusinessException;
import com.studyjun.lottoweb.exception.ErrorCode;
import com.studyjun.lottoweb.repository.AnswerRepository;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.util.DefaultAssert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public Message createQuestion(UserPrincipal userPrincipal, CreateQuestionRequest createQuestionRequest) {
        User user = getUser(userPrincipal.getId());

        Question question = Question.builder()
                .subject(createQuestionRequest.getSubject())
                .content(createQuestionRequest.getContent())
                .author(user)
                .createdDate(LocalDateTime.now())
                .isPrivate(createQuestionRequest.getIsPrivate())
                .build();

        questionRepository.save(question);

        return Message.builder().message("고객센터에 질문이 등록되었습니다.").build();
    }

    public QuestionPageResponse getAllQuestions(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> questions = questionRepository.findAll(pageable);
        return toQuestionPageResponse(questions);
    }

    public QuestionPageResponse getMyQuestions(UserPrincipal userPrincipal, int page) {
        User user = getUser(userPrincipal.getId());

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> questions = questionRepository.findByAuthorId(pageable, user.getId());
        return toQuestionPageResponse(questions);
    }

    public QuestionDetailResponse showQuestionDetail(long id, UserPrincipal userPrincipal) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        DefaultAssert.isTrue(questionOptional.isPresent(), "질문을 찾을 수 없습니다.");

        Question question = questionOptional.get();

        User user = getUser(userPrincipal.getId());

        if (!question.isPrivate() || user.getRole().equals("ADMIN") || question.getAuthor().equals(user)) {
            return QuestionDetailResponse.from(question);
        }

        throw new BusinessException(AuthErrorCode.FORBIDDEN, "비밀글입니다.");
    }

    public Message createAnswer(long questionId, UserPrincipal userPrincipal, CreateAnswerRequest createAnswerRequest) {
        User user = getUser(userPrincipal.getId());

        boolean isManager = user.getRole().equals("ADMIN");
        DefaultAssert.isTrue(isManager, "관리자 권한이 없습니다.");

        Optional<Question> questionOptional = questionRepository.findById(questionId);
        DefaultAssert.isTrue(questionOptional.isPresent(), "질문을 찾을 수 없습니다.");

        Answer answer = Answer.builder()
                .subject(createAnswerRequest.getSubject())
                .content(createAnswerRequest.getContent())
                .createdDate(LocalDateTime.now())
                .question(questionOptional.get())
                .build();

        answerRepository.save(answer);

        return Message.builder().message("답변이 등록되었습니다.").build();
    }

    private QuestionPageResponse toQuestionPageResponse(Page<Question> questions) {
        return QuestionPageResponse.builder()
                .content(questions.getContent().stream().map(QuestionSummaryResponse::from).toList())
                .page(questions.getNumber())
                .size(questions.getSize())
                .totalElements(questions.getTotalElements())
                .totalPages(questions.getTotalPages())
                .build();
    }

    private User getUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");
        return userOptional.get();
    }
}