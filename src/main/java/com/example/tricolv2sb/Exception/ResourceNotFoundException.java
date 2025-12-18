package com.example.tricolv2sb.Exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 * Used for: User, Product, Supplier, PurchaseOrder, GoodsIssue, StockMovement,
 * Role, etc.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
