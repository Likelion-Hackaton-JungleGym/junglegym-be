package com.hackathon.junglegym.domain.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.bill.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {}
