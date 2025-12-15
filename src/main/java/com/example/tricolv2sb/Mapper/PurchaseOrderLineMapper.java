package com.example.tricolv2sb.Mapper;

import com.example.tricolv2sb.DTO.purchaseorder.ReadPurchaseOrderLineDTO;
import com.example.tricolv2sb.Entity.PurchaseOrderLine;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PurchaseOrderLineMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "purchaseOrderId", source = "purchaseOrder.id")
    ReadPurchaseOrderLineDTO toDto(PurchaseOrderLine purchaseOrderLine);
}
