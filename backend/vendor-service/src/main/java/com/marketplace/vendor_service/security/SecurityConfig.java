package com.marketplace.vendor_service.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  // Add this annotation
public class SecurityConfig {

    private final JwtService jwt;
    private final JwtAuthFilter jwtAuthFilter;  // Inject directly

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
                .requestMatchers(HttpMethod.POST, "/api/vendor/apply").hasRole("USER")
                .requestMatchers("/api/vendor/pending").hasRole("ADMIN")
                .requestMatchers("/api/vendor/approve/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()  // For H2 console
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); // For H2 console

        return http.build();
    }
}