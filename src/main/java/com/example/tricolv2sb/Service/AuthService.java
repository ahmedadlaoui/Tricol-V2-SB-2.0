package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Security.JwtService;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuthServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAppRepository userRepository;

    @Override
    public AuthenticationResponse signUserIn(AuthenticationRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserApp user = (UserApp) authentication.getPrincipal();
        String JWT = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(JWT)
                .build();
    }
}
