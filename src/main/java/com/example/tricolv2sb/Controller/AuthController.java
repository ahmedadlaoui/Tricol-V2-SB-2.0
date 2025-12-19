package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.authentication.AuthenticationRequest;
import com.example.tricolv2sb.DTO.authentication.AuthenticationResponse;
import com.example.tricolv2sb.DTO.authentication.RegisterRequest;
import com.example.tricolv2sb.DTO.common.ApiResponse;
import com.example.tricolv2sb.DTO.userapp.ReadUserDTO;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Mapper.UserAppMapper;
import com.example.tricolv2sb.Security.CookieUtil;
import com.example.tricolv2sb.Security.JwtService;
import com.example.tricolv2sb.Service.ServiceInterfaces.AuthServiceInterface;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceInterface authService;
    private final UserAppMapper userAppMapper;
    private final CookieUtil cookieUtil;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse httpResponse) {
        AuthenticationResponse response = authService.signUserIn(request);

        String refreshToken = ((com.example.tricolv2sb.Service.AuthService) authService)
                .getRefreshTokenForUser(response.getEmail());
        if (refreshToken != null) {
            int maxAge = (int) (jwtService.getRefreshExpiration() / 1000);
            cookieUtil.setRefreshTokenCookie(httpResponse, refreshToken, maxAge);
        }

        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse httpResponse) {
        AuthenticationResponse response = authService.register(request);

        String refreshToken = ((com.example.tricolv2sb.Service.AuthService) authService)
                .getRefreshTokenForUser(response.getEmail());
        if (refreshToken != null) {
            int maxAge = (int) (jwtService.getRefreshExpiration() / 1000);
            cookieUtil.setRefreshTokenCookie(httpResponse, refreshToken, maxAge);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Registration successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = extractRefreshTokenFromCookie(httpRequest);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Refresh token not found"));
        }

        try {
            AuthenticationResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            cookieUtil.clearRefreshTokenCookie(httpResponse);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = extractRefreshTokenFromCookie(httpRequest);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(refreshToken);
        }

        cookieUtil.clearRefreshTokenCookie(httpResponse);
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<ReadUserDTO>> getCurrentUser(@AuthenticationPrincipal UserApp user) {
        ReadUserDTO userDTO = userAppMapper.toReadUserDTO(user);
        return ResponseEntity.ok(ApiResponse.success(userDTO, "Current user fetched successfully"));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieUtil.getCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
