package com.hackathon.junglegym.domain.qeustion.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.qeustion.dto.request.QuestionRequest;
import com.hackathon.junglegym.domain.qeustion.dto.response.QuestionResponse;
import com.hackathon.junglegym.domain.qeustion.service.QuestionService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Question", description = "정글톡 api")
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping("/chat")
  public ResponseEntity<BaseResponse<QuestionResponse>> chat(
      @Parameter(description = "질문 내용", example = "서울시장 뭐시기") @Valid @RequestBody
          QuestionRequest request) {
    QuestionResponse response = questionService.chat(request);

    return ResponseEntity.ok(BaseResponse.success("질문 성공", response));
  }

  @GetMapping("/chat")
  public ResponseEntity<BaseResponse<List<QuestionResponse>>> get10Chats() {
    return ResponseEntity.ok(BaseResponse.success(questionService.get10Chats()));
  }
}
