package com.hackathon.junglegym.domain.promiseCategory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse;
import com.hackathon.junglegym.domain.promiseCategory.entity.PromiseCategory;

public interface PromiseCategoryRepository extends JpaRepository<PromiseCategory, Long> {

  List<PromiseCategory> findByPolitician_IdOrderByIdAsc(Long politicianId);

  @Query(
      """
            select new com.hackathon.junglegym.domain.promiseCategory.dto.response.PromiseCategoryResponse(
              c.id, c.title, c.content
            )
            from PromiseCategory c
            where c.politician.id = :politicianId
            order by c.id asc
          """)
  List<PromiseCategoryResponse> findPromiseCategoriesByPoliticianId(
      @Param("politicianId") Long politicianId);

  boolean existsById(Long id);
}
