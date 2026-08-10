package com.example.backend.dto.returns;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReturnRequestProcessRequest {
    private String status;          // APPROVED / REJECTED / COMPLETED
    private BigDecimal refundAmount;
    private String note;
}