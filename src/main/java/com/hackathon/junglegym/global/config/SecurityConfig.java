package com.hackathon.junglegym.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfig corsConfig;

  // private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 보호 기능 비활성화 (REST API에서는 필요없음)
        .csrf(AbstractHttpConfigurer::disable)
        // CORS 설정 활성화(보통은 CORS 설정 활성화 하지 않음. 서버에서 NginX로 CORS 검증)
        .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
        // HTTP Basic 인증 기본 설정
        .httpBasic(Customizer.withDefaults())
        // 세션을 생성하지 않음 (JWT 사용으로 인한 Stateless 설정)
        // .sessionManagement(
        // session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // HTTP 요청에 대한 권한 설정
        .authorizeHttpRequests(
            request ->
                request
                    // ✅ 프리플라이트(OPTIONS) 전역 허용 — CORS 사전요청 통과
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    // Swagger 경로 인증 필요
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    // 인증 없이 허용할 경로
                    .requestMatchers("/api/**")
                    .permitAll()
                    // 그 외 모든 요청은 모두 인증 필요
                    .anyRequest()
                    .permitAll() // 모든 요청 허용
            );
    // .authenticated())
    // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
