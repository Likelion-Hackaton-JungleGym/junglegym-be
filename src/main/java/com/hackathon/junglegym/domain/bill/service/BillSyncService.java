package com.hackathon.junglegym.domain.bill.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.hackathon.junglegym.domain.bill.entity.Bill;
import com.hackathon.junglegym.domain.bill.repository.BillRepository;
import com.hackathon.junglegym.domain.politician.entity.Politician;
import com.hackathon.junglegym.domain.politician.entity.Role;
import com.hackathon.junglegym.domain.politician.exception.PoliticianErrorCode;
import com.hackathon.junglegym.domain.politician.repository.PoliticianRepository;
import com.hackathon.junglegym.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillSyncService {

  private final BillOpenApi billOpenApi;
  private final BillRepository billRepository;
  private final PoliticianRepository politicianRepository;
  private final BillDetailScraper billDetailScraper;
  private final OpenAiBillSummaryService openAiBillSummaryService;

  /** 의원 1명 기준 동기화 (대표발의자/제안자 이름으로 조회) */
  @Transactional
  public int syncBillsOfPolitician(Long politicianId) throws Exception {
    Politician p =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (p.getRole() != Role.NATIONAL_ASSEMBLY) {
      return 0;
    }

    List<JsonNode> rows = billOpenApi.fetchAllByProposer(p.getName(), "row");

    int saved = 0;
    for (JsonNode n : rows) {
      String assemblyBillId = billOpenApi.text(n, "BILL_ID"); // 고유ID (필수)
      if (assemblyBillId == null) {
        continue;
      }

      // 기본 필드 매핑
      String name = billOpenApi.text(n, "BILL_NAME");
      String proposeDt = billOpenApi.text(n, "PROPOSE_DT"); // yyyyMMdd / yyyy-MM-dd
      String result = billOpenApi.text(n, "PROC_RESULT");
      String detailLink = billOpenApi.text(n, "DETAIL_LINK");
      String mainProp = billOpenApi.text(n, "RST_PROPOSER"); // 대표발의자
      String joinProp = billOpenApi.text(n, "PUBL_PROPOSER"); // 공동발의자

      LocalDate proposeDate = parseDate(proposeDt);

      // upsert by assemblyBillId
      Bill bill =
          billRepository
              .findByAssemblyBillId(assemblyBillId)
              .orElseGet(() -> Bill.builder().assemblyBillId(assemblyBillId).politician(p).build());
      boolean isNew = (bill.getId() == null);

      bill.updateFromOpenApi(
          p,
          assemblyBillId,
          name,
          proposeDate,
          result,
          detailLink,
          mainProp,
          joinProp,
          bill.getMainContent(), // mainContent (상세 API 사용 시 채우기)
          bill.getSummaryContent() // summaryContent
          );

      billRepository.save(bill);

      // 1) bill의 mainContent 채우기 (주요 내용 스크랩)
      if (bill.getMainContent() == null || bill.getMainContent().isBlank()) {
        fillMainContentIfPossible(bill);
      }

      // 2) summaryContent가 비어있으면 OpenAI 요약 시도
      if (bill.getSummaryContent() == null || bill.getSummaryContent().isBlank()) {
        fillSummaryIfPossible(bill);
      }

      saved++;
    }

    log.info("법률안 동기화 완료 politicianId={} saved={}", politicianId, saved);
    return saved;
  }

  /** yyyyMMdd / yyyy-MM-dd → LocalDate */
  private LocalDate parseDate(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      if (raw.matches("\\d{8}")) {
        return LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyyMMdd"));
      }
      if (raw.matches("\\d{4}-\\d{2}-\\d{2}")) {
        return LocalDate.parse(raw);
      }
    } catch (Exception ignore) {
    }
    return null;
  }

  private void fillMainContentIfPossible(Bill bill) {
    if (bill.getDetailLink() == null) {
      return;
    }
    try {
      String content = billDetailScraper.fetchMainContent(bill.getDetailLink());
      if (content != null && !content.isBlank()) {
        bill.updateMainContent(content);
        billRepository.save(bill); // 더티체킹으로도 가능하지만 명시 저장
      }
    } catch (Exception e) {
      // 네트워크 실패해도 동기화 전체가 깨지지 않게 워닝만
      log.warn("[BILL] 상세 본문 스크랩 실패 id={} url={}", bill.getId(), bill.getDetailLink(), e);
    }
  }

  /** mainContent가 있고 summaryContent가 비어있을 때만 OpenAI 요약 */
  private void fillSummaryIfPossible(Bill bill) {
    // 조건: 본문이 있어야 하고 요약은 비어있어야 함
    if ((bill.getMainContent() == null || bill.getMainContent().isBlank())
        || (bill.getSummaryContent() != null && !bill.getSummaryContent().isBlank())) {
      return;
    }
    try {
      String summary =
          openAiBillSummaryService.summarizeTo3Lines(bill.getName(), bill.getMainContent());
      if (summary != null && !summary.isBlank()) {
        bill.updateSummaryContent(summary);
        billRepository.save(bill);
      }
    } catch (Exception e) {
      log.warn("[BILL] 요약 생성 실패 id={} title={}", bill.getId(), bill.getName(), e);
    }
  }

  // 이미 저장된 . 중 main_content가 비어있는 것만 채우는 배치 (보충용)
  @Transactional
  public int backfillMainContents() {
    List<Bill> targets = billRepository.findAllByMainContentIsNull(); // 쿼리 메서드 추가 필요
    int filled = 0;
    for (Bill b : targets) {
      fillMainContentIfPossible(b);
      filled++;
      // 과도한 호출 방지를 위한 짧은 슬립
      try {
        Thread.sleep(150);
      } catch (InterruptedException ignored) {
      }
    }
    log.info("[BILL] main_content 백필 완료 count={}", filled);
    return filled;
  }
}
