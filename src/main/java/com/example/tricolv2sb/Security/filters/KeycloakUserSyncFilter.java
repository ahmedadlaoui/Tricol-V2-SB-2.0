package com.example.tricolv2sb.Security.filters;

import com.example.tricolv2sb.Entity.Enum.RoleName;
import com.example.tricolv2sb.Entity.RoleApp;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Repository.RoleAppRepository;
import com.example.tricolv2sb.Repository.UserAppRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final UserAppRepository userAppRepository;
    private final RoleAppRepository roleAppRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            UserApp dbUser = syncUser(jwt);

            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                    dbUser,
                    jwt,
                    jwtAuth.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        filterChain.doFilter(request, response);
    }

    private UserApp syncUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        Optional<UserApp> existingUser = userAppRepository.findByKeycloakId(keycloakId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        String email = jwt.getClaimAsString("email");
        String fullName = jwt.getClaimAsString("name");
        if (email == null || email.isEmpty()) {
            email = jwt.getClaimAsString("preferred_username");
        }

        RoleName roleName = getRoleFromJwt(jwt);
        RoleApp role = roleAppRepository.findByName(roleName).orElse(null);

        UserApp newUser = UserApp.builder()
                .keycloakId(keycloakId)
                .email(email)
                .fullName(fullName)
                .password("KEYCLOAK_MANAGED")
                .isActive(true)
                .role(role)
                .build();

        return userAppRepository.save(newUser);
    }

    @SuppressWarnings("unchecked")
    private RoleName getRoleFromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return RoleName.STOREKEEPER;
        Object rolesObj = realmAccess.get("roles");
        if (rolesObj instanceof Collection) {
            Collection<String> roles = (Collection<String>) rolesObj;
            if (roles.contains("ADMIN")) return RoleName.ADMIN;
            if (roles.contains("PURCHASING_MANAGER")) return RoleName.PURCHASING_MANAGER;
            if (roles.contains("WORKSHOP_MANAGER")) return RoleName.WORKSHOP_MANAGER;
            if (roles.contains("STOREKEEPER")) return RoleName.STOREKEEPER;
        }
        return RoleName.STOREKEEPER;
    }
}