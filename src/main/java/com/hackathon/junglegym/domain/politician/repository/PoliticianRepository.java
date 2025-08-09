package com.hackathon.junglegym.domain.politician.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface PoliticianRepository extends JpaRepository<Politician, Long> {}
