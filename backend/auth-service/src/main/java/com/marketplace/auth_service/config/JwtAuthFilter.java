package com.marketplace.auth_service.config;

import com.marketplace.auth_service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.startsWith("/auth/")) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.validateToken(token)) {
                String email = jwtService.extractEmail(token);
                String role = jwtService.extractRole(token);

                // Normalize role to avoid mismatches with hasRole("ADMIN") / hasAuthority("ROLE_ADMIN")
                SimpleGrantedAuthority authority = null;
                if (role != null) {
                    String normalized = role.trim().toUpperCase(Locale.ROOT);
                    if (normalized.startsWith("ROLE_")) {
                        authority = new SimpleGrantedAuthority(normalized);
                    } else {
                        authority = new SimpleGrantedAuthority("ROLE_" + normalized);
                    }
                }

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authority == null ? List.of() : List.of(authority)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }
}