package com.studyjun.lottoweb.dto.response;

import com.studyjun.lottoweb.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionDetailResponse {
    private Long id;
    private String subject;
    private String content;
    private String authorNickname;
    private boolean isPrivate;
    private LocalDateTime createdDate;

    public static QuestionDetailResponse from(Question question) {
        return QuestionDetailResponse.builder()
                .id(question.getId())
                .subject(question.getSubject())
                .content(question.getContent())
                .authorNickname(question.getAuthor().getNickname())
                .isPrivate(question.isPrivate())
                .createdDate(question.getCreatedDate())
                .build();
    }
}