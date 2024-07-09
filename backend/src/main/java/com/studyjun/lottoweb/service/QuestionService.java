package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.request.CreateAnswerRequest;
import com.studyjun.lottoweb.dto.request.CreateQuestionRequest;
import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.Answer;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
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

    public ResponseEntity<?> createQuestion(UserPrincipal userPrincipal, CreateQuestionRequest createQuestionRequest) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        Question question = Question.builder()
                .subject(createQuestionRequest.getSubject())
                .content(createQuestionRequest.getContent())
                .author(userOptional.get())
                .createdDate(LocalDateTime.now())
                .isPrivate(createQuestionRequest.getIsPrivate())
                .build();

        questionRepository.save(question);

        ApiResponse apiResponse = ApiResponse.builder().check(true).information(Message.builder().message("고객센터에 질문이 등록되었습니다.").build()).build();
        return ResponseEntity.ok(apiResponse);
    }

    public Page<Question> getAllQuestions(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findAll(pageable);
    }

    public Page<Question> getMyQuestions(UserPrincipal userPrincipal, int page) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findByAuthorId(pageable, userPrincipal.getId());
    }

    public ResponseEntity<?> showQuestionDetail(long id, UserPrincipal userPrincipal) {
        Optional<Question> questionOptional = questionRepository.findById(id);
        DefaultAssert.isTrue(questionOptional.isPresent(), "질문을 찾을 수 없습니다.");

        Question question = questionOptional.get();

        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");
        User user = userOptional.get();

        if (!question.isPrivate() || user.getRole().equals("ADMIN") || question.getAuthor().equals(user)) {
            return ResponseEntity.ok(question);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "비밀글입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    public ResponseEntity<?> createAnswer(long questionId, UserPrincipal userPrincipal, CreateAnswerRequest createAnswerRequest) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());

        boolean isManager = userOptional.get().getRole().equals("ADMIN");
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

        ApiResponse apiResponse = ApiResponse.builder().check(true).information(Message.builder().message("답변이 등록되었습니다.").build()).build();

        return ResponseEntity.ok(apiResponse);
    }
}