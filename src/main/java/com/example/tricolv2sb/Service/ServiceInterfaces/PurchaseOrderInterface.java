package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.purchaseorder.CreatePurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderDTO;
import com.example.tricolv2sb.DTO.purchaseorder.UpdatePurchaseOrderDTO;

import java.util.List;

public interface PurchaseOrderInterface {

    List<ReadPurchaseOrderDTO> getAllPurchaseOrders();

    ReadPurchaseOrderDTO getPurchaseOrderById(Long id);

    ReadPurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderDTO createPurchaseOrderDTO);

    ReadPurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderDTO updatePurchaseOrderDTO);

    void deletePurchaseOrder(Long id);

    List<ReadPurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId);

    void validateOrder(Long orderId);

    void cancelOrder(Long orderId);

    void receiveOrder(Long orderId);
}
