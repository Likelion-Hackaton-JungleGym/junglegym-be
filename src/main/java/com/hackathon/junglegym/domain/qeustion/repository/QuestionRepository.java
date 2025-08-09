package com.hackathon.junglegym.domain.qeustion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.qeustion.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {}
