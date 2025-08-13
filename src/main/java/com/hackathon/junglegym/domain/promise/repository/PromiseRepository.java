package com.hackathon.junglegym.domain.promise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.promise.entity.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {

  List<Promise> findByCategory_Politician_Id(Long politicianId);
}
