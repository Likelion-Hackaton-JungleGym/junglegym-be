package com.hackathon.junglegym.domain.qeustion.mapper;

import org.springframework.stereotype.Component;

import com.hackathon.junglegym.domain.qeustion.dto.response.QuestionResponse;
import com.hackathon.junglegym.domain.qeustion.entity.Question;

@Component
public class QuestionMapper {

  public static QuestionResponse toQuestionResponse(Question question) {
    return QuestionResponse.builder()
        .question(question.getQuestion())
        .answer(question.getAnswer())
        .constitution(question.getConstitution())
        .build();
  }
}
