package com.example.tricolv2sb.DTO.purchaseorder;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReadPurchaseOrderLineDTO {
    private Long id;
    private Integer quantity;
    private Double unitPrice;
    private Long purchaseOrderId;
    private Long productId;
    private String productName;
}
