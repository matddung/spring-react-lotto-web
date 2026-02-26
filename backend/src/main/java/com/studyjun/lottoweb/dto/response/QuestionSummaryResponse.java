package com.studyjun.lottoweb.dto.response;

import com.studyjun.lottoweb.entity.Question;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionSummaryResponse {
    private Long id;
    private String subject;
    private String authorNickname;
    private boolean isPrivate;
    private LocalDateTime createdDate;

    public static QuestionSummaryResponse from(Question question) {
        return QuestionSummaryResponse.builder()
                .id(question.getId())
                .subject(question.getSubject())
                .authorNickname(question.getAuthor().getNickname())
                .isPrivate(question.isPrivate())
                .createdDate(question.getCreatedDate())
                .build();
    }
}