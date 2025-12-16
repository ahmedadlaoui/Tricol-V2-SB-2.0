package com.example.tricolv2sb.Security;


import com.example.tricolv2sb.Entity.RoleApp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String GenerateJwtToken(String email, RoleApp role, Long userId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",userId);
        claims.put("role",role);
        return createToken(claims,email);
    }

}
