package com.hackathon.junglegym.domain.criminalRecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.criminalRecord.entity.CriminalRecord;

public interface CriminalRecordRepository extends JpaRepository<CriminalRecord, Long> {}
