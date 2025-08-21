package com.hackathon.junglegym.domain.qeustion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.qeustion.dto.request.QuestionRequest;
import com.hackathon.junglegym.domain.qeustion.dto.response.QuestionResponse;
import com.hackathon.junglegym.domain.qeustion.entity.Question;
import com.hackathon.junglegym.domain.qeustion.mapper.QuestionMapper;
import com.hackathon.junglegym.domain.qeustion.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

  private final EmbeddingService embeddingService;
  private final QdrantService qdrantService;
  private final ChatCompletionService chat;
  private final QuestionRepository questionRepository;
  private final String NO_MATCH_MSG = "해당 질문과 관련된 법 조항을 찾을 수 없습니다.";

  @Transactional
  public QuestionResponse chat(QuestionRequest request) {

    // 1. 질문 -> 임베딩
    final String question = request.getQuestion();
    final float[] qvec = embeddingService.embed(question);

    // 2. Qdrant 검색 -> 관련 법 조항 Top 1 만 사용
    var hitOpt = qdrantService.searchTop1WithScore(qvec);

    // 3. GPT 답변 생성
    String relatedLaw = null;

    double similarityThreshold = 0.4;
    if (hitOpt.isEmpty()
        || hitOpt.get().score() < similarityThreshold
        || hitOpt.get().text().isBlank()) {
      relatedLaw = NO_MATCH_MSG;
    } else {
      relatedLaw = hitOpt.get().text();
    }

    String answer = chat.answer(question, relatedLaw, 700);

    // 4. DB 저장 (privated = false일 때 또는 관련 법 조항을 찾았을 때)
    if (!request.isPrivated() && !NO_MATCH_MSG.equals(relatedLaw)) {
      try {
        Question q =
            Question.builder().question(question).answer(answer).constitution(relatedLaw).build();
        questionRepository.save(q);
      } catch (Exception e) {
        log.warn("질문/답변 저장 실패: {}", e.toString());
      }
    }

    return QuestionResponse.builder()
        .question(question)
        .answer(answer)
        .constitution(relatedLaw)
        .build();
  }

  public List<QuestionResponse> get10Chats() {
    List<Question> list = questionRepository.findTop10ByOrderByCreatedAtDesc();
    List<QuestionResponse> questionResponseList = new ArrayList<>();
    for (Question q : list) {
      QuestionResponse response = QuestionMapper.toQuestionResponse(q);
      questionResponseList.add(response);
    }

    return questionResponseList;
  }
}
