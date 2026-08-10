package com.example.backend.dto.returns;

import lombok.Data;
import java.util.List;

@Data
public class ReturnRequestCreateRequest {
    private Integer orderId;
    private String requestType;    // EXCHANGE / RETURN
    private String reason;
    private List<ReturnRequestItemRequest> items;
}