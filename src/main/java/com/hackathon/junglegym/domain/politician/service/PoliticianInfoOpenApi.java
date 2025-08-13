package com.hackathon.junglegym.domain.politician.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.junglegym.domain.politician.exception.OpenApiErrorCode;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PoliticianInfoOpenApi {

  private static final Logger log = LoggerFactory.getLogger(PoliticianInfoOpenApi.class);

  @Value("${openapi.service-key}")
  private String serviceKey;

  @Value("${openapi.info.base-url}")
  private String baseUrl;

  private final int pSize = 50;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public List<JsonNode> fetchAllPages(String arrayKey) throws Exception {
    List<JsonNode> all = new ArrayList<>();
    int page = 1;

    while (true) {
      HttpURLConnection conn = null;

      try {
        // 1. URL 설정
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        // 2. url 요청 규격 설정
        urlBuilder
            .append("?")
            .append(URLEncoder.encode("KEY", StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("type", StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode("json", StandardCharsets.UTF_8));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("pIndex", StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode(String.valueOf(page), StandardCharsets.UTF_8));
        urlBuilder
            .append("&")
            .append(URLEncoder.encode("pSize", StandardCharsets.UTF_8))
            .append("=")
            .append(URLEncoder.encode(String.valueOf(pSize), StandardCharsets.UTF_8));

        // 3. url 객체 생성
        URL url = (new URI(urlBuilder.toString())).toURL();
        // 4. Connection 객체 생성
        conn = (HttpURLConnection) url.openConnection();
        // 5. 통신을 위한 메소드 set
        conn.setRequestMethod("GET");
        // 6. 통신을 위한 Content-type set
        conn.setRequestProperty("Content-type", "application/json");

        int code;
        try {
          code = conn.getResponseCode();
        } catch (Exception e) {
          log.error("[OPENAPI] 응답코드를 가져올 수 없습니다.", e);
          throw new CustomException(OpenApiErrorCode.OPENAPI_HTTP_ERROR);
        }

        InputStream body = (code >= 400) ? conn.getErrorStream() : conn.getInputStream();

        if (body == null) {
          log.error("[OPENAPI] page={} code={} body=null", page, code);
          throw new CustomException(OpenApiErrorCode.OPENAPI_EMPTY_BODY);
        }
        if (code >= 400) {
          log.error("[OPENAPI] page={} code={} body={}", page, code, body);
          throw new CustomException(OpenApiErrorCode.OPENAPI_HTTP_ERROR);
        }

        JsonNode root;
        try (body) {
          root = objectMapper.readTree(body);
        } catch (Exception e) {
          log.error("[OPENAPI] 파싱 에러, page={}", page, e);
          throw new CustomException(OpenApiErrorCode.OPENAPI_PARSE_ERROR);
        }

        JsonNode arr = root.findPath(arrayKey);

        if (arr.isMissingNode() || arr.isNull()) {
          log.error("[OPENAPI] arrayKey={}, page={}", arrayKey, page);
          throw new CustomException(OpenApiErrorCode.OPENAPI_ARRAY_KEY_MISSING);
        }

        int got = 0;
        if (arr.isArray()) {
          for (JsonNode n : arr) {
            all.add(n);
            got++;
          }
        } else {
          all.add(arr);
          got = 1;
        }

        if (got < pSize) {
          break;
        } // 마지막 페이지
        page++;
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }
    return all;
  }

  public String text(JsonNode n, String field) {
    JsonNode f = n.get(field);
    return (f == null || f.isNull() || f.asText().isBlank()) ? null : f.asText().trim();
  }
}
