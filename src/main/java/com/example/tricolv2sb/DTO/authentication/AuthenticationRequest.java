package com.example.tricolv2sb.DTO.authentication;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationRequest {

    private String email;
    private String password;

}
