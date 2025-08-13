package com.hackathon.junglegym.domain.bill.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.hackathon.junglegym.domain.bill.dto.response.BillResponse.BillListItemResponse;
import com.hackathon.junglegym.domain.bill.dto.response.BillResponse.PagedBillPageResponse;
import com.hackathon.junglegym.domain.bill.entity.Bill;

public class BillMapper {

  public static BillListItemResponse toBillListItemResponse(Bill bill) {
    return new BillListItemResponse(
        bill.getId(),
        bill.getName(),
        bill.getMainContent(),
        bill.getProposeDate(),
        bill.getMainProposer(),
        bill.getJoinProposer(),
        bill.getDetailLink());
  }

  public static PagedBillPageResponse toPagedBillPageResponse(Page<Bill> page) {
    List<BillListItemResponse> items =
        page.getContent().stream()
            .map(BillMapper::toBillListItemResponse)
            .collect(Collectors.toList());

    return new PagedBillPageResponse(
        page.getNumber() + 1, // 0-base → 1-base
        page.getSize(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.hasPrevious(),
        page.hasNext(),
        items);
  }
}
