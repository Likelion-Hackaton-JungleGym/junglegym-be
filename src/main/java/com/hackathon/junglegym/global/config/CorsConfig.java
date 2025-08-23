package com.hackathon.junglegym.global.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed-origins}")
  private String[] allowedOrigins;

  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 환경 변수에 정의된 출처만 허용
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    // 리스트에 작성한 HTTP 메소드 요청만 허용
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    // 리스트에 작성한 헤더들이 포함된 요청만 허용
    // configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept"));
    // 헤더: 실무 편의상 우선 전부 허용(필요시 좁히면 됨)
    configuration.setAllowedHeaders(Arrays.asList("*"));
    // 쿠키나 인증 정보를 포함하는 요청 허용
    configuration.setAllowCredentials(true);
    // 프리플라이트 캐시 시간(초) - 선택
    configuration.setMaxAge(3600L);
    // 모든 경로에 대해 위의 CORS 설정을 적용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
