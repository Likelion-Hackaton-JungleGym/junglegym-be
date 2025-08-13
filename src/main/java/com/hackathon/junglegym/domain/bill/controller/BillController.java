package com.hackathon.junglegym.domain.bill.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.junglegym.domain.bill.dto.response.BillResponse.PagedBillPageResponse;
import com.hackathon.junglegym.domain.bill.service.BillService;
import com.hackathon.junglegym.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Bill", description = "발의법률안 관련 API")
public class BillController {

  private final BillService billService;

  @Operation(summary = "정치인의 발의법률안 조회 (최신순)", description = "전채 발의법률안을 10개식 페이징해 보여줍니다.")
  @GetMapping("/politicians/{politicianId}/bills")
  public ResponseEntity<BaseResponse<PagedBillPageResponse>> listBillOfPolitician(
      @PathVariable Long politicianId, @RequestParam(defaultValue = "1") int page) {
    if (page < 1) { // page 하한 방어(음수/0 들어와도 1로 보정)
      page = 1;
    }
    PagedBillPageResponse billPageResponse = billService.listBillsOfPolitician(politicianId, page);
    return ResponseEntity.ok(BaseResponse.success("발의법률안 한 페이지 조회 성공", billPageResponse));
  }
}
