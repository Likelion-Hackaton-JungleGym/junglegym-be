package com.hackathon.junglegym.domain.bill.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hackathon.junglegym.domain.bill.dto.response.BillResponse.PagedBillPageResponse;
import com.hackathon.junglegym.domain.bill.entity.Bill;
import com.hackathon.junglegym.domain.bill.mapper.BillMapper;
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
public class BillService {

  private static final int PAGE_SIZE = 10;

  private final BillRepository billRepository;
  private final PoliticianRepository politicianRepository;

  public PagedBillPageResponse listBillsOfPolitician(Long politicianId, int page1Base) {
    Politician p =
        politicianRepository
            .findById(politicianId)
            .orElseThrow(() -> new CustomException(PoliticianErrorCode.POLITICIAN_NOT_FOUND));

    if (p.getRole() != Role.NATIONAL_ASSEMBLY) {
      return BillMapper.toPagedBillPageResponse(Page.empty(PageRequest.of(0, PAGE_SIZE)));
    }

    int zeroBase = Math.max(page1Base, 1) - 1;
    PageRequest pageable =
        PageRequest.of(zeroBase, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "proposeDate"));

    Page<Bill> pageData = billRepository.findByPolitician(p, pageable);
    return BillMapper.toPagedBillPageResponse(pageData);
  }
}
