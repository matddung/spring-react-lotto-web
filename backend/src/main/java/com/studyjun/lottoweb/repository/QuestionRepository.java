package com.studyjun.lottoweb.repository;

import com.studyjun.lottoweb.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAll(Pageable pageable);
    Page<Question> findByAuthorId(Pageable pageable, Long id);
    List<Question> findByAuthorId(Long id);
    Page<Question> findByAuthorIdOrderByCreatedDateDesc(Pageable pageable, Long id);
    Page<Question> findByAnswerIsNull(Pageable pageable);
}
