package com.example.tricolv2sb.DTO.purchaseorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreatePurchaseOrderDTO {
    
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;
    
    @NotEmpty(message = "Order lines are required")
    @Valid
    private List<OrderLineDTO> orderLines;
    
    @Data
    @NoArgsConstructor
    public static class OrderLineDTO {
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private Double quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
        private Double unitPrice;

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
    }
}