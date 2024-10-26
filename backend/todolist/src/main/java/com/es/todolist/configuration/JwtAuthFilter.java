package com.es.todolist.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.es.todolist.services.JwtUtilService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.es.todolist.configuration.CustomUserDetails;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtilService jwtUtil;

    @Autowired
    public JwtAuthFilter(@Lazy JwtUtilService jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * This method is called for each request that comes to the server
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the token from the header
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        List<String> roles = null;

        // Check if the token is not null and starts with Bearer
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);

            try {
                // Validate the token
                if (jwtUtil.validateToken(token)) {
                    // Extract username from token
                    String usersub = jwtUtil.extractUserSub(token);

                    String usename = jwtUtil.extractUsername(token);

                    String email = jwtUtil.extractEmail(token);

                    System.out.println("UserSub: " + usersub);

                    // Create CustomUserDetails object
                    CustomUserDetails userDetails = new CustomUserDetails(usersub, usename, email);

                    // Create UsernamePasswordAuthenticationToken without authorities
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, // principal (username)
                            null, // credentials
                            null // authorities (no roles needed)
                    );

                    // Set the authentication details
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authenticated user in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // If the token is invalid or an exception occurs, clear the security context
                SecurityContextHolder.clearContext();
                e.printStackTrace();
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
