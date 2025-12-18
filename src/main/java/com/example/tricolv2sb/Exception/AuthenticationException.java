package com.example.tricolv2sb.Exception;

/**
 * Exception thrown when authentication fails.
 * Used for: invalid credentials, disabled account, etc.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
