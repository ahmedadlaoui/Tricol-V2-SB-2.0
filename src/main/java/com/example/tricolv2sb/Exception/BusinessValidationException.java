package com.example.tricolv2sb.Exception;

/**
 * Exception thrown when a business rule is violated.
 * Used for: invalid state transitions, insufficient stock, cannot delete with
 * dependencies, etc.
 */
public class BusinessValidationException extends RuntimeException {
    public BusinessValidationException(String message) {
        super(message);
    }
}
