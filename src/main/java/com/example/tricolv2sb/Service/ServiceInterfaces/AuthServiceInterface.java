package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;
import com.example.tricolv2sb.DTO.authentication.RegisterRequest;

public interface AuthServiceInterface {
    AuthenticationResponse signUserIn(AuthenticationRequest request);

    AuthenticationResponse register(RegisterRequest request);
}
