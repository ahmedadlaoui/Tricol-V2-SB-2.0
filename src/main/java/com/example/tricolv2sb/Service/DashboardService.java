package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.dashboard.DashboardDTO;
import com.example.tricolv2sb.DTO.dashboard.RecentActivityDTO;
import com.example.tricolv2sb.DTO.dashboard.StockAlertDTO;
import com.example.tricolv2sb.Entity.Enum.GoodsIssueStatus;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import com.example.tricolv2sb.Entity.GoodsIssue;
import com.example.tricolv2sb.Entity.Product;
import com.example.tricolv2sb.Entity.PurchaseOrder;
import com.example.tricolv2sb.Entity.StockLot;
import com.example.tricolv2sb.Repository.*;
import com.example.tricolv2sb.Service.ServiceInterfaces.DashboardServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardServiceInterface {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final GoodsIssueRepository goodsIssueRepository;
    private final StockLotRepository stockLotRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTO getDashboardStats() {
        // Stock Overview
        List<Product> products = productRepository.findAll();
        List<StockLot> allLots = stockLotRepository.findAll();

        int totalProducts = products.size();
        int totalStockLots = (int) allLots.stream()
                .filter(lot -> lot.getRemainingQuantity() > 0)
                .count();

        double totalStockValue = allLots.stream()
                .filter(lot -> lot.getRemainingQuantity() > 0)
                .mapToDouble(lot -> lot.getRemainingQuantity() * lot.getPurchasePrice())
                .sum();

        // Calculate products with low stock
        List<StockAlertDTO> stockAlerts = getStockAlerts();
        int productsLowStock = stockAlerts.size();

        // Purchase Orders Overview
        List<PurchaseOrder> allOrders = purchaseOrderRepository.findAll();
        int totalPurchaseOrders = allOrders.size();
        int pendingOrders = (int) allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        int validatedOrders = (int) allOrders.stream().filter(o -> o.getStatus() == OrderStatus.VALIDATED).count();
        int deliveredOrders = (int) allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        int cancelledOrders = (int) allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();

        // Goods Issues Overview
        List<GoodsIssue> allIssues = goodsIssueRepository.findAll();
        int totalGoodsIssues = allIssues.size();
        int draftIssues = (int) allIssues.stream().filter(i -> i.getStatus() == GoodsIssueStatus.DRAFT).count();
        int validatedIssues = (int) allIssues.stream().filter(i -> i.getStatus() == GoodsIssueStatus.VALIDATED).count();
        int cancelledIssues = (int) allIssues.stream().filter(i -> i.getStatus() == GoodsIssueStatus.CANCELLED).count();

        // Suppliers Overview
        int totalSuppliers = (int) supplierRepository.count();

        // Recent Activities
        List<RecentActivityDTO> recentActivities = getRecentActivities(allOrders, allIssues);

        return DashboardDTO.builder()
                .totalProducts(totalProducts)
                .totalStockLots(totalStockLots)
                .totalStockValue(totalStockValue)
                .productsLowStock(productsLowStock)
                .totalPurchaseOrders(totalPurchaseOrders)
                .pendingOrders(pendingOrders)
                .validatedOrders(validatedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalGoodsIssues(totalGoodsIssues)
                .draftIssues(draftIssues)
                .validatedIssues(validatedIssues)
                .cancelledIssues(cancelledIssues)
                .totalSuppliers(totalSuppliers)
                .recentActivities(recentActivities)
                .stockAlerts(stockAlerts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertDTO> getStockAlerts() {
        List<Product> products = productRepository.findAll();
        List<StockAlertDTO> alerts = new ArrayList<>();

        for (Product product : products) {
            Double totalStock = stockLotRepository.calculateTotalAvailableStock(product.getId());
            if (totalStock == null)
                totalStock = 0.0;

            Double reorderPoint = product.getReorderPoint();

            if (totalStock <= reorderPoint) {
                String alertLevel = totalStock == 0 ? "CRITICAL" : "WARNING";
                String message = totalStock == 0
                        ? "Out of stock! Immediate reorder required."
                        : String.format("Stock below reorder point (%.0f). Current: %.0f", reorderPoint, totalStock);

                alerts.add(StockAlertDTO.builder()
                        .productId(product.getId())
                        .productReference(product.getReference())
                        .productName(product.getName())
                        .currentStock(totalStock)
                        .reorderPoint(reorderPoint)
                        .alertLevel(alertLevel)
                        .message(message)
                        .build());
            }
        }

        // Sort: CRITICAL first, then WARNING
        alerts.sort(Comparator.comparing(StockAlertDTO::getAlertLevel));
        return alerts;
    }

    private List<RecentActivityDTO> getRecentActivities(List<PurchaseOrder> orders, List<GoodsIssue> issues) {
        List<RecentActivityDTO> activities = new ArrayList<>();

        // Add recent purchase orders
        orders.stream()
                .sorted(Comparator.comparing(PurchaseOrder::getOrderDate).reversed())
                .limit(5)
                .forEach(order -> activities.add(RecentActivityDTO.builder()
                        .type("PURCHASE_ORDER")
                        .action(order.getStatus().name())
                        .reference("PO-" + order.getId())
                        .date(order.getOrderDate())
                        .description("Purchase order " + order.getStatus().name().toLowerCase() +
                                " - " + order.getSupplier().getName())
                        .build()));

        // Add recent goods issues
        issues.stream()
                .sorted(Comparator.comparing(GoodsIssue::getIssueDate).reversed())
                .limit(5)
                .forEach(issue -> activities.add(RecentActivityDTO.builder()
                        .type("GOODS_ISSUE")
                        .action(issue.getStatus().name())
                        .reference(issue.getIssueNumber())
                        .date(issue.getIssueDate())
                        .description("Goods issue " + issue.getStatus().name().toLowerCase() +
                                " - " + issue.getDestination())
                        .build()));

        // Sort by date and return top 10
        return activities.stream()
                .sorted(Comparator.comparing(RecentActivityDTO::getDate).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
