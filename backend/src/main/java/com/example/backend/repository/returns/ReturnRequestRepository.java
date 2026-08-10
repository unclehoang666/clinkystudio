package com.example.backend.repository.returns;

import com.example.backend.entity.returns.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {

    List<ReturnRequest> findByOrder_User_IdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT r FROM ReturnRequest r WHERE (:status IS NULL OR r.status = :status)")
    Page<ReturnRequest> search(@Param("status") String status, Pageable pageable);
}