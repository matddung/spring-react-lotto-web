package com.studyjun.lottoweb.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDateTime createdDate;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Answer answer;

    private boolean isPrivate;

    @Builder
    public Question(String subject, String content, User author, LocalDateTime createdDate, boolean isPrivate) {
        this.subject = subject;
        this.content = content;
        this.author = author;
        this.createdDate = createdDate;
        this.isPrivate = isPrivate;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}