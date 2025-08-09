package com.hackathon.junglegym.domain.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {}
