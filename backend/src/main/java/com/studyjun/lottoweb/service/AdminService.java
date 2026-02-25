package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.HistoryResponse;
import com.studyjun.lottoweb.dto.response.Message;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import com.studyjun.lottoweb.security.UserPrincipal;
import com.studyjun.lottoweb.util.DefaultAssert;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public ResponseEntity<?> getAllUsers(UserPrincipal userPrincipal, int page) {
        isAdmin(userPrincipal);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.builder().check(true).data(users).build());
    }

    public ResponseEntity<?> getUserHistory(Long id, UserPrincipal userPrincipal, int page) {
        isAdmin(userPrincipal);

        Optional<User> userOptional = userRepository.findById(id);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        User user = userOptional.get();
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> questions = questionRepository.findByAuthorIdOrderByCreatedDateDesc(pageable, user.getId());

        HistoryResponse historyResponse = HistoryResponse.builder()
                .user(user)
                .question(questions)
                .build();

        return ResponseEntity.ok(ApiResponse.builder().check(true).data(historyResponse).build());
    }

    public ResponseEntity<?> getUnansweredQuestions(UserPrincipal userPrincipal, int page) {
        isAdmin(userPrincipal);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Question> questions = questionRepository.findByAnswerIsNull(pageable);
        return ResponseEntity.ok(ApiResponse.builder().check(true).data(questions).build());
    }

    public ResponseEntity<?> deleteUser(Long id, UserPrincipal userPrincipal) {
        isAdmin(userPrincipal);

        Optional<User> userOptional = userRepository.findById(id);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        User user = userOptional.get();

        List<Question> questions = questionRepository.findByAuthorId(user.getId());
        questionRepository.deleteAll(questions);

        userRepository.delete(user);

        return ResponseEntity.ok(ApiResponse.builder().check(true).data(Message.builder().message("유저 삭제가 완료되었습니다.").build()).build());
    }

    public void isAdmin(UserPrincipal userPrincipal) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        User user = userOptional.get();

        boolean isAdmin = user.getRole().equals("ADMIN");
        DefaultAssert.isTrue(isAdmin, "ADMIN 계정이 아닙니다.");
    }
}