package com.example.tricolv2sb.Service;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;
import com.example.tricolv2sb.DTO.authentication.RegisterRequest;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Repository.UserAppRepository;
import com.example.tricolv2sb.Security.JwtService;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuthServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponse signUserIn(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserApp user = (UserApp) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        UserApp user = UserApp.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isActive(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
