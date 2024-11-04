package com.es.todolist.configuration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String userSub;
    private final String username;
    private final String email;

    public CustomUserDetails(String userSub, String username, String email) {
        this.userSub = userSub;
        this.username = username;
        this.email = email;
    }

    public String getUserSub() {
        return userSub;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // or assign roles if you have any
    }

    @Override
    public String getPassword() {
        return null; // Not required for JWT-based authentication
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }
}
