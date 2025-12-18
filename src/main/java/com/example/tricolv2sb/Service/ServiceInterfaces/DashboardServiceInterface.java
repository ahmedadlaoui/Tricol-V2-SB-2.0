package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.dashboard.DashboardDTO;
import com.example.tricolv2sb.DTO.dashboard.StockAlertDTO;

import java.util.List;

public interface DashboardServiceInterface {
    DashboardDTO getDashboardStats();

    List<StockAlertDTO> getStockAlerts();
}
