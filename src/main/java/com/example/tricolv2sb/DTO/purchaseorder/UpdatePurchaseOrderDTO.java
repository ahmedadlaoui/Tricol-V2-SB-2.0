package com.example.tricolv2sb.DTO.purchaseorder;

import com.example.tricolv2sb.Entity.Enum.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePurchaseOrderDTO {
    
    @NotNull(message = "Status is required")
    private OrderStatus status;
    
    private Double totalAmount;
    private LocalDateTime receptionDate;
    
    @Valid
    private List<OrderLineDTO> orderLines;
    
    @Data
    @NoArgsConstructor
    public static class OrderLineDTO {
        private Long id;

        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private Double quantity;

        @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
        private Double unitPrice;

        @Positive(message = "Product ID must be positive")
        private Long productId;
    }
}