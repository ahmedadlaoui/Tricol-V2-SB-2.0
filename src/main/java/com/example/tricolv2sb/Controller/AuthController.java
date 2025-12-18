package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;
import com.example.tricolv2sb.DTO.authentication.RegisterRequest;
import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuthServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceInterface authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.signUserIn(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Registration successful"));
    }
}
