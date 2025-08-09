package com.hackathon.junglegym.domain.promise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.promise.entity.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {}
