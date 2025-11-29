package com.example.tricolv2sb.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreatePurchaseOrderDTO {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @Valid
    private List<OrderLineItemDTO> orderLines;
}