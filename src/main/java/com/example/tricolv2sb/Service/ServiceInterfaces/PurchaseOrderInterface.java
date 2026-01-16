package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;
import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseOrderInterface {

    Page<ReadPurchaseOrderDTO> getAllPurchaseOrders(Pageable pageable);

    List<ReadPurchaseOrderDTO> getPurchaseOrdersByStatus(OrderStatus status);

    ReadPurchaseOrderDTO getPurchaseOrderById(Long id);

    ReadPurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO);

    ReadPurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderDTO updatePurchaseOrderDTO);

    void deletePurchaseOrder(Long id);

    List<ReadPurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId);

    void validateOrder(Long orderId);

    void cancelOrder(Long orderId);

    void receiveOrder(Long orderId);
}
