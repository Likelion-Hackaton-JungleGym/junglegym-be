package com.hackathon.junglegym.domain.qeustion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.qeustion.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

  List<Question> findTop10ByOrderByCreatedAtDesc();
}
