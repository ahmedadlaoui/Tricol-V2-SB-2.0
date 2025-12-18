package com.example.tricolv2sb.Exception;

/**
 * Exception thrown when a business rule is violated.
 * Used for: invalid state transitions, insufficient stock, cannot delete with
 * dependencies, etc.
 */
public class BusinessViolationException extends RuntimeException {
    public BusinessViolationException(String message) {
        super(message);
    }
}
