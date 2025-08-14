package com.hackathon.junglegym.domain.bill.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.bill.entity.Bill;
import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface BillRepository extends JpaRepository<Bill, Long> {

  Page<Bill> findByPolitician(Politician politician, Pageable pageable);

  Optional<Bill> findByAssemblyBillId(String assemblyBillId);

  List<Bill> findAllByMainContentIsNull();
}
