package com.studyjun.lottoweb.service;

import com.studyjun.lottoweb.dto.response.HistoryResponse;
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

    public Page<User> getAllUsers(UserPrincipal userPrincipal, int page) {
        isAdmin(userPrincipal);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findAll(pageable);
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

        return ResponseEntity.ok(historyResponse);
    }

    public Page<Question> getUnansweredQuestions(UserPrincipal userPrincipal, int page) {
        isAdmin(userPrincipal);
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepository.findByAnswerIsNull(pageable);
    }

    public ResponseEntity<?> deleteUser(Long id, UserPrincipal userPrincipal) {
        isAdmin(userPrincipal);

        Optional<User> userOptional = userRepository.findById(id);
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        User user = userOptional.get();

        List<Question> questions = questionRepository.findByAuthorId(user.getId());
        questionRepository.deleteAll(questions);

        userRepository.delete(user);

        return ResponseEntity.ok(true);
    }

    public void isAdmin(UserPrincipal userPrincipal) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(userOptional.isPresent(), "사용자를 찾을 수 없습니다.");

        User user = userOptional.get();

        boolean isAdmin = user.getRole().equals("ADMIN");
        DefaultAssert.isTrue(isAdmin, "ADMIN계정이 아닙니다.");
    }
}