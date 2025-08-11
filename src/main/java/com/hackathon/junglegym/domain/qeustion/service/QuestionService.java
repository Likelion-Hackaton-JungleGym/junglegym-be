package com.hackathon.junglegym.domain.qeustion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.junglegym.domain.qeustion.dto.request.QuestionRequest;
import com.hackathon.junglegym.domain.qeustion.dto.response.QuestionResponse;
import com.hackathon.junglegym.domain.qeustion.entity.Question;
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

  @Transactional
  public QuestionResponse chat(QuestionRequest request) {

    // 1. 질문 -> 임베딩
    final String question = request.getQuestion();
    final float[] qvec = embeddingService.embed(question);

    // 2. Qdrant 검색 -> 관련 법 조항 Top 1 만 사용
    String relatedLaw = qdrantService.searchTop1(qvec);

    // 3. 컨텍스트 구성
    String context = relatedLaw == null ? "" : relatedLaw;

    // 4. GPT 답변 생성
    String answer;
    if (context.isBlank()) {
      answer = "제공된 자료에서 관련 내용을 찾지 못했습니다. 질문을 더 구체적으로 작성해주시면 감사하겠습니다.";
    } else {
      answer = chat.answer(question, context, 700);
    }

    // 5. DB 저장 (is_privated = false일 때만)
    if (!request.isPrivated()) {
      try {
        String constitution = stripSnippet(relatedLaw);
        Question q =
            Question.builder().question(question).answer(answer).constitution(constitution).build();
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

  private static String stripSnippet(String best) {
    if (best == null) return null;
    int idx = best.indexOf('"');
    if (idx >= 0) return best.substring(0, idx).trim();
    return best.trim();
  }
}
