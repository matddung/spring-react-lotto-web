package com.studyjun.lottoweb.repository;

import com.studyjun.lottoweb.entity.Question;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAll(Sort sort);
    List<Question> findByAuthorId(Long id);
    List<Question> findByAuthorIdOrderByCreatedDateDesc(Long id);
    List<Question> findByAnswerIsNull();
}
