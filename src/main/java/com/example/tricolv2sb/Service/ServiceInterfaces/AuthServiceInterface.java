package com.example.tricolv2sb.Service.ServiceInterfaces;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;

public interface AuthServiceInterface {
    public AuthenticationResponse signUserIn(AuthenticationRequest request);
}
