package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.dashboard.DashboardDTO;
import com.example.tricolv2sb.DTO.dashboard.StockAlertDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.DashboardServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardServiceInterface dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        DashboardDTO dashboard = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard data fetched successfully"));
    }

    @GetMapping("/alerts")
    @PreAuthorize("hasAuthority('STOCK:READ')")
    public ResponseEntity<ApiResponse<List<StockAlertDTO>>> getStockAlerts() {
        List<StockAlertDTO> alerts = dashboardService.getStockAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts, "Stock alerts fetched successfully"));
    }
}
