package com.example.tricolv2sb.Config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(
            "ADMIN", Set.of(
                    "STOCK:CREATE", "STOCK:READ", "STOCK:UPDATE", "STOCK:DELETE",
                    "SUPPLIER:CREATE", "SUPPLIER:READ", "SUPPLIER:UPDATE", "SUPPLIER:DELETE",
                    "PRODUCT:CREATE", "PRODUCT:READ", "PRODUCT:UPDATE", "PRODUCT:DELETE",
                    "STOCK_MOVEMENT:CREATE", "STOCK_MOVEMENT:READ", "STOCK_MOVEMENT:UPDATE", "STOCK_MOVEMENT:DELETE",
                    "GOODS_ISSUE:CREATE", "GOODS_ISSUE:READ", "GOODS_ISSUE:UPDATE", "GOODS_ISSUE:DELETE",
                    "GOODS_ISSUE:VALIDATE", "GOODS_ISSUE:CANCEL",
                    "PUCHASE_ORDER:CREATE", "PUCHASE_ORDER:READ", "PUCHASE_ORDER:UPDATE", "PUCHASE_ORDER:DELETE",
                    "PUCHASE_ORDER:RECEIVE", "PUCHASE_ORDER:VALIDATE", "PUCHASE_ORDER:  CANCEL",
                    "USER:CREATE", "USER:READ", "USER:UPDATE", "USER:DELETE", "AUDIT_LOGS:READ"),
            "PURCHASING_MANAGER", Set.of(
                    "SUPPLIER:CREATE", "SUPPLIER:READ", "SUPPLIER:UPDATE", "SUPPLIER:DELETE",
                    "PRODUCT:READ", "STOCK:READ",
                    "PUCHASE_ORDER:CREATE", "PUCHASE_ORDER:READ", "PUCHASE_ORDER:UPDATE", "PUCHASE_ORDER:DELETE",
                    "PUCHASE_ORDER:RECEIVE", "PUCHASE_ORDER:VALIDATE", "PUCHASE_ORDER:CANCEL"),
            "STOREKEEPER", Set.of(
                    "STOCK:CREATE", "STOCK:READ", "STOCK:UPDATE", "STOCK:DELETE",
                    "STOCK_MOVEMENT:CREATE", "STOCK_MOVEMENT:READ", "STOCK_MOVEMENT:UPDATE", "STOCK_MOVEMENT:DELETE",
                    "PRODUCT:READ", "PUCHASE_ORDER:READ", "PUCHASE_ORDER:RECEIVE"),
            "WORKSHOP_MANAGER", Set.of(
                    "GOODS_ISSUE:CREATE", "GOODS_ISSUE:READ", "GOODS_ISSUE:UPDATE", "GOODS_ISSUE:DELETE",
                    "GOODS_ISSUE:VALIDATE", "GOODS_ISSUE:CANCEL",
                    "STOCK:READ", "STOCK_MOVEMENT:READ", "PRODUCT:READ"));

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Set<String> roles = new HashSet<>();

       
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection) {
            roles.addAll((Collection<String>) realmAccess.get("roles"));
        }

        
        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaims().get("resource_access");
        if (resourceAccess != null) {
            resourceAccess.values().stream()
                    .filter(v -> v instanceof Map)
                    .map(v -> (Map<String, Object>) v)
                    .map(m -> m.get("roles"))
                    .filter(r -> r instanceof Collection)
                    .forEach(r -> roles.addAll((Collection<String>) r));
        }

  
        for (String role : roles) {
            String upperRole = role.toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + upperRole));

            Set<String> perms = ROLE_PERMISSIONS.get(upperRole);
            if (perms != null) {
                perms.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
            }
        }

        return authorities;
    }
}