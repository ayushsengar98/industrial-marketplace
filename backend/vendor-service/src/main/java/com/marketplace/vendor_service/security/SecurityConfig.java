package com.marketplace.vendor_service.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwt;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtService jwt, JwtAuthFilter jwtAuthFilter){
        this.jwt = jwt;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/h2-console/**").permitAll()
                
                // USER can apply and check status
                .requestMatchers(HttpMethod.POST, "/api/vendor/apply").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/vendor/status").hasAnyRole("USER", "VENDOR")
                
                // ADMIN only endpoints
                .requestMatchers(HttpMethod.GET, "/api/vendor/pending").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/vendor/approve/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/vendor/reject/**").hasRole("ADMIN")  // 🔴 ADD THIS
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
