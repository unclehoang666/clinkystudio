package com.example.backend.repository.catalog;

import com.example.backend.entity.catalog.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    List<Category> findByStatusTrue();

    List<Category> findByParentIsNull();

    List<Category> findByParent_Id(Integer parentId);

    @Query("SELECT c FROM Category c WHERE " +
           "(:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Category> search(@Param("q") String q, @Param("status") Boolean status, Pageable pageable);
}