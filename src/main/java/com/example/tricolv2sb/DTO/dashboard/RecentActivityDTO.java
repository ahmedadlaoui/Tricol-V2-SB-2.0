package com.example.tricolv2sb.DTO.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private String type; // PURCHASE_ORDER, GOODS_ISSUE, STOCK_MOVEMENT
    private String action; // CREATED, VALIDATED, DELIVERED, CANCELLED
    private String reference; // Order number or issue number
    private LocalDate date;
    private String description;
}
