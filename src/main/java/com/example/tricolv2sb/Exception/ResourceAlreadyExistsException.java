package com.example.tricolv2sb.Exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Used for: duplicate User email, duplicate Product reference, duplicate
 * Supplier ICE, etc.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
