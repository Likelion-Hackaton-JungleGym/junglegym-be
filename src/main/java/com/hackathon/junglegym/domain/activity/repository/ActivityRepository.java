package com.hackathon.junglegym.domain.activity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.junglegym.domain.activity.entity.Activity;
import com.hackathon.junglegym.domain.politician.entity.Politician;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

  List<Activity> findByPolitician(Politician politician);

  List<Activity> findByPoliticianId(Long politicianId);
}
