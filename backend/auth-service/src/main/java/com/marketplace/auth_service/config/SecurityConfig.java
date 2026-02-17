package com.marketplace.auth_service.config;

import com.marketplace.auth_service.service.JwtService;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService){
        this.jwtService=jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter) throws Exception{

        http
            .csrf(csrf->csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            .sessionManagement(s->
                    s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

           .authorizeHttpRequests(auth->auth
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/h2-console/**").permitAll()

    .requestMatchers("/api/user/**").hasAnyRole("USER","ADMIN","VENDOR")

    .requestMatchers("/api/vendor/apply").hasRole("USER")
    .requestMatchers("/api/vendor/pending").hasRole("ADMIN")
    .requestMatchers("/api/vendor/approve/**").hasRole("ADMIN")
    .requestMatchers("/api/vendor/reject/**").hasRole("ADMIN")

    .requestMatchers("/api/admin/**").hasRole("ADMIN")

    .anyRequest().authenticated()
)
            .addFilterBefore(jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class)

            .formLogin(f->f.disable())
            .httpBasic(b->b.disable());

        return http.build();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(jwtService);
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
