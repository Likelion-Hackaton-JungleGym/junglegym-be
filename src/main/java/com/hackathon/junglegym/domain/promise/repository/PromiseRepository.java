package com.hackathon.junglegym.domain.promise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse;
import com.hackathon.junglegym.domain.promise.entity.Promise;

public interface PromiseRepository extends JpaRepository<Promise, Long> {

  List<Promise> findByCategory_Politician_Id(Long politicianId);

  @Query(
      """
        select new com.hackathon.junglegym.domain.promise.dto.response.PromiseResponse(
          p.id, p.name, p.progress, p.goal
        )
        from Promise p
        where p.category.id = :categoryId
        order by p.id asc
      """)
  List<PromiseResponse> findPromisesByCategoryId(@Param("categoryId") Long categoryId);
}
