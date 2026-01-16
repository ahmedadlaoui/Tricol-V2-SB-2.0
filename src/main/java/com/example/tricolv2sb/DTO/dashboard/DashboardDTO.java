package com.example.tricolv2sb.DTO.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private Integer totalProducts;
    private Integer totalStockLots;
    private Double totalStockValue;
    private Integer productsLowStock;

    private Integer totalPurchaseOrders;
    private Integer pendingOrders;
    private Integer validatedOrders;
    private Integer deliveredOrders;
    private Integer cancelledOrders;

    private Integer totalGoodsIssues;
    private Integer draftIssues;
    private Integer validatedIssues;
    private Integer cancelledIssues;

    private Integer totalSuppliers;

    // Recent Activity
    private List<RecentActivityDTO> recentActivities;

    // Stock Alerts
    private List<StockAlertDTO> stockAlerts;
}
