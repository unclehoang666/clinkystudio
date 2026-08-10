package com.example.backend.repository.returns;

import com.example.backend.entity.returns.ReturnRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnRequestItemRepository extends JpaRepository<ReturnRequestItem, Integer> {
    List<ReturnRequestItem> findByReturnRequest_Id(Integer returnRequestId);
}