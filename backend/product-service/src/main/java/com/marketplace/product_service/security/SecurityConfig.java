package com.marketplace.product_service.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain filter(HttpSecurity http) throws Exception {

        http
        .csrf(cs->cs.disable())

        .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth->auth

            .requestMatchers(HttpMethod.POST,"/api/products").hasRole("VENDOR")
            .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("VENDOR")
            .requestMatchers("/api/products/mine").hasRole("VENDOR")

            .requestMatchers("/api/products/**").permitAll()

            .anyRequest().authenticated()
        )

        .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
