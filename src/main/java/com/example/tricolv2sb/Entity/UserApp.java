package com.example.tricolv2sb.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "user_app")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApp implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleApp role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPermission> customPermissions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new java.util.HashSet<>();

        if (role != null) {
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName().name()));

            if (role.getPermissions() != null) {
                for (Permission p : role.getPermissions()) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(p.getAuthority()));
                }
            }
        }

        if (customPermissions != null) {
            for (UserPermission up : customPermissions) {
                String authName = up.getPermission().getAuthority();
                if (up.isGranted()) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(authName));
                } else {
                    authorities.removeIf(auth -> auth.getAuthority().equals(authName));
                }
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}