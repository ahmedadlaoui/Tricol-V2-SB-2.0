package com.example.tricolv2sb.Security;

import com.example.tricolv2sb.Entity.Permission;
import com.example.tricolv2sb.Entity.UserApp;
import com.example.tricolv2sb.Entity.UserPermission;
import com.example.tricolv2sb.Repository.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserAppRepository userAppRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserApp user = userAppRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User is blocked");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserApp user) {
        Set<String> authorities = new HashSet<>();

        authorities.add("ROLE_" + user.getRole().getName().name());

        for (Permission p : user.getRole().getPermissions()) {
            authorities.add(p.getRessource().name() + ":" + p.getAction().name());
        }

        if (user.getCustomPermissions() != null) {
            for (UserPermission up : user.getCustomPermissions()) {
                String authName = up.getPermission().getRessource().name() + ":" + up.getPermission().getAction().name();

                if (up.isGranted()) {
                    authorities.add(authName);
                } else {
                    authorities.remove(authName);
                }
            }
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}