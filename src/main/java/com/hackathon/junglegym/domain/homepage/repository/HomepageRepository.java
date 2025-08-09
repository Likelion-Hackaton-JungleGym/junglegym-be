package com.hackathon.junglegym.domain.homepage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.homepage.entity.Homepage;

public interface HomepageRepository extends JpaRepository<Homepage, Long> {}
