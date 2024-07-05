package com.studyjun.lottoweb.config;

import com.studyjun.lottoweb.entity.Answer;
import com.studyjun.lottoweb.entity.Question;
import com.studyjun.lottoweb.entity.User;
import com.studyjun.lottoweb.repository.AnswerRepository;
import com.studyjun.lottoweb.repository.QuestionRepository;
import com.studyjun.lottoweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.save(User.builder()
                .email("admin@admin.a")
                .password(encoder.encode("admin"))
                .nickname("관리자")
                .role("ADMIN")
                .provider(Provider.local)
                .providerId("local.admin")
                .build());

        userRepository.save(User.builder()
                .email("string@aa.bb")
                .nickname("string")
                .password(encoder.encode("string"))
                .provider(Provider.local)
                .role("USER")
                .build());

        userRepository.save(User.builder()
                .email("string1@aa.bb")
                .nickname("string1")
                .password(encoder.encode("string"))
                .provider(Provider.local)
                .role("USER")
                .build());

        tempQuestion();

        answerRepository.save(Answer.builder()
                .question(questionRepository.findById(1000L).get())
                .subject("답변 제목")
                .content("답변 내용")
                .createdDate(LocalDateTime.now())
                .build());
    }

    public void tempQuestion() {
        for (int i = 1; i <= 1000; i++) {
            if (i % 2 == 0) {
                questionRepository.save(Question.builder()
                        .isPrivate(true)
                        .createdDate(LocalDateTime.now())
                        .subject(i + "번 게시물 제목")
                        .content(i + "번 게시물 내용")
                        .author(userRepository.findById(2L).get())
                        .build());
            } else {
                questionRepository.save(Question.builder()
                        .isPrivate(false)
                        .createdDate(LocalDateTime.now())
                        .subject(i + "번 게시물 제목")
                        .content(i + "번 게시물 내용")
                        .author(userRepository.findById(3L).get())
                        .build());
            }
        }
    }
}
