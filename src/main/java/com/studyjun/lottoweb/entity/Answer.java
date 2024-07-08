package com.studyjun.lottoweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@Entity
@Getter
@NoArgsConstructor
public class Answer {
    @Id
    private Long questionId;

    private String subject;

    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question;

    private LocalDateTime createdDate;

    @Builder
    public Answer(String subject, String content, Question question, LocalDateTime createdDate) {
        this.subject = subject;
        this.content = content;
        this.question = question;
        this.createdDate = createdDate;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}