package com.ansh.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Override
    public String getPassword() {
        return password;
    }

    // ── UserDetails ───────────────────────────────────────────────────────────
    @Override public String getUsername()                              { return email; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public boolean isAccountNonExpired()                     { return true; }
    @Override public boolean isAccountNonLocked()                      { return true; }
    @Override public boolean isCredentialsNonExpired()                 { return true; }
    @Override public boolean isEnabled()                               { return true; }

}
