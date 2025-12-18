package com.example.tricolv2sb.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T body;

    public static <T> ApiResponse<T> success(T body, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(message)
                .body(body)
                .build();
    }

    public static <T> ApiResponse<T> created(T body, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message(message)
                .body(body)
                .build();
    }
}
