package com.example.tricolv2sb.DTO.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAlertDTO {
    private Long productId;
    private String productReference;
    private String productName;
    private Double currentStock;
    private Double reorderPoint;
    private String alertLevel; // CRITICAL, WARNING
    private String message;
}
