package com.hackathon.junglegym.domain.politician.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PoliticianInfoOpenApi {

  @Value("${openapi.service-key}")
  private String serviceKey;

  @Value("${openapi.info.base-url}")
  private String baseUrl;

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  // 타깃 정치인 목록

}
